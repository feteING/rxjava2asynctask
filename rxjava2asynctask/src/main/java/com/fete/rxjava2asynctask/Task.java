package com.fete.rxjava2asynctask;

/**
 * 在IO与UI线程执行任务
 */
public abstract class Task<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public Task(T t) {
        setT(t);
    }

    public Task() {
    }

    public abstract void preIO();

    public abstract void updateUI();
}
