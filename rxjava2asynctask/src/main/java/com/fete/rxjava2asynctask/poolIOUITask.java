package com.fete.rxjava2asynctask;

/**
 * 在IO与UI线程执行任务
 */
public abstract class PoolIOUITask<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public PoolIOUITask(T t) {
        setT(t);
    }

    public PoolIOUITask() {
    }

    public abstract boolean preIO();

    public abstract void updateUI();
}
