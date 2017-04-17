package com.fete.rxjava2asynctask;


import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Rxjava封装工具类
 * 1.开个io线程，开个ui线程
 * 2.支持io耗时,ui更新（多线程间流式处理io执行结果再执行ui）
 * 3.支持destory中断处理不处理ui更新也不会报错(activity关闭更新ui)
 */
public class Rxjava2 {


    /**
     * io线程
     *
     * @param task
     * @return
     */
    public static CompositeDisposable execute(IOTask task) {
        CompositeDisposable disposables = new CompositeDisposable();
        Disposable subscribe = Observable.just(task)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<IOTask>() {
                    @Override
                    public void accept(IOTask t) throws Exception {
                        t.doInIOThread();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposables;
    }

    /**
     * ui线程
     *
     * @param task
     * @return
     */
    public static CompositeDisposable execute(UITask task) {
        CompositeDisposable disposables = new CompositeDisposable();
        Disposable subscribe = Observable.just(task)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UITask>() {
                    @Override
                    public void accept(UITask t) throws Exception {
                        t.doInUIThread();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });

        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposables;
    }


    /**
     * @param task 步骤执行,1->2
     *             第一步io执行后才执行步骤2
     *             传递数据2种方法
     *             1.setT()getT
     *             2.调用处全局变量
     *             不用flowable 只产生一次e.onNext();
     *             preIO()与updateUI不用进行错误捕获
     */
    public static CompositeDisposable execute(final Task task) {
        CompositeDisposable disposables = new CompositeDisposable();
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                task.preIO();
                Object t = task.getT();
                if (t == null) t = "";
                e.onNext(t);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        task.setT(o);
                        task.updateUI();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposables;

    }

    static CompositeDisposable disposables = new CompositeDisposable();
    static int mCount;
    static long mTime;
    static boolean inUi = true;

    /**
     * pool  ui线程每隔second执行一次task,共执行count次
     */
    public static CompositeDisposable poolInUi(int count, long second, final PoolTask task) {
        if (disposables != null) {
            disposables.clear();
        }
        mCount = count;
        mTime = second;
        inUi = true;
        Disposable subscribe = Observable.just(task)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PoolTask>() {
                    @Override
                    public void accept(PoolTask t) throws Exception {
                        boolean b = t.execute();
                        if (!b && mCount > 0) {
                            mCount--;
                            delayAgain(t, inUi);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });

        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposables;

    }

    /**
     * pool  io线程每隔second执行一次task,共执行count次
     */
    public static CompositeDisposable poolInIo(int count, long second, final PoolTask task) {
        if (disposables != null) {
            disposables.clear();
        }
        mCount = count;
        mTime = second;
        inUi = false;
        Disposable subscribe = Observable.just(task)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<PoolTask>() {
                    @Override
                    public void accept(PoolTask t) throws Exception {
                        boolean b = t.execute();
                        if (!b && mCount > 0) {
                            mCount--;
                            delayAgain(t, inUi);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });

        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposables;

    }

    /**
     * pool  io线程每隔second执行一次task,共执行count次,执行成功ui线程更新
     */
    public static CompositeDisposable poolInIOUI(int count, long second, final FLowPoolTask task) {
        if (disposables != null) {
            disposables.clear();
        }
        mCount = count;
        mTime = second;
        inUi = false;
        tmp = false;

        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                boolean b = task.preIO();
                if (!b && mCount > 0) {
                    mCount--;
                    delayAgain(task, inUi);


                }
                Object t = task.getT();
                if (t == null) t = "";


                while (true) {
                    try {
                        Thread.sleep(32);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (tmp) {
                        e.onNext(t);
                        e.onComplete();
                        return;
                    }
                    if (mCount <= 0) {
                        cancelPool();
                        return;
                    }
                }


            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        task.setT(o);
                        task.updateUI();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                    }
                });

        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return disposables;

    }


    /**
     * ui线程
     *
     * @param task
     * @param inUi 是否在ui线程 ture是在ui线程
     */
    private static void delayAgain(PoolTask task, final boolean inUi) {
        Disposable subscribe = Observable.just(task)
                .delay(mTime, TimeUnit.SECONDS)
                .observeOn(inUi ? AndroidSchedulers.mainThread() : Schedulers.io())
                .subscribe(new Consumer<PoolTask>() {
                    @Override
                    public void accept(PoolTask t) throws Exception {
                        int c = mCount;
                        Log.e("count___", c-- + "");
                        boolean b = t.execute();
                        if (!b && mCount > 0) {
                            mCount--;
                            delayAgain(t, inUi);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });

        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    static boolean tmp = false;

    /**
     * ui线程
     *
     * @param task
     * @param inUi 是否在ui线程 ture是在ui线程
     */
    private static void delayAgain(FLowPoolTask task, final boolean inUi) {

        Disposable subscribe = Observable.just(task)
                .delay(mTime, TimeUnit.SECONDS)
                .observeOn(inUi ? AndroidSchedulers.mainThread() : Schedulers.io())
                .subscribe(new Consumer<FLowPoolTask>() {
                    @Override
                    public void accept(FLowPoolTask t) throws Exception {
                        int c = mCount;
                        Log.e("count___", c-- + "");
                        boolean b = t.preIO();
                        if (!b && mCount > 0) {
                            mCount--;
                            delayAgain(t, inUi);
                        }
                        if (b) {
                            tmp = true;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });

        try {
            disposables.add(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 取消pool
     */
    public static void cancelPool() {
        if (disposables != null) {
            disposables.clear();
        }
        mCount = 0;
        mTime = 0;

    }


}


