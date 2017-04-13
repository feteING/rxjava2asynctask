package com.fete.rxjava2asynctask;

/**
 * 在主线程中执行的任务
 */
public abstract class UITask<T> {

    public abstract void doInUIThread();


    public UITask() {

    }

    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

}