package com.fete.rxjava2asynctask;

/**
 * 在IO与UI线程执行任务
 */
public abstract class FLowPoolTask<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public FLowPoolTask(T t) {
        setT(t);
    }

    public FLowPoolTask() {
    }

    public abstract boolean preIO();

    public abstract void updateUI();
}
