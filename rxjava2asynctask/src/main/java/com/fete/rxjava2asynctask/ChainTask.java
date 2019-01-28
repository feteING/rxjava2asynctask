package com.fete.rxjava2asynctask;

/**
 * 在IO线程中执行的任务
 */
public abstract class ChainTask<T> {
    private T t;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public ChainTask() {
    }

    public ChainTask(int type) {
        this.type = type;
    }

    public abstract void doThread();
}