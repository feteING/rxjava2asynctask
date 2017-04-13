package com.fete.rxjava2task;

/**
 * 在IO线程中执行的任务
 */
public abstract class IOTask<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public IOTask() {
    }

    public abstract void doInIOThread();
}