package com.fete.rxjava2task;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/*

String name = "";
    CompositeDisposable disposable;
    public void test() {

        disposable = Rxjava2.execute(new Rxjava2.Task() {
            @Override
            public void preIO() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                name = "yhf";
                //                setT(new TestModel("yhf"));
            }

            @Override
            public void updateUI() {
//                TestModel t = (TestModel) getT();
                Toast.makeText(ViewsActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //中断
        if (disposable != null) {
            disposable.clear();
        }
    }*/


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


}


