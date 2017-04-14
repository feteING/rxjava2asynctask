package com.fete.rxjava2asynctask;

/**
 * 在主线程中执行的任务
 */
public abstract class PoolTask<T> {

    public abstract boolean execute();


    public PoolTask() {

    }

    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

}