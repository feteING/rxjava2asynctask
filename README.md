# rxjava2asynctask  
1.io处理，ui处理，io处理ui更新  
2.中断处理 ui io task (释放处理)  
3.线程间流式处理    
4.轮询执行 (插入u盘，反应较慢，尝试3次，如果成功执行，失败抛出错误)  
io处理完，才开启ui处理，不用担心io没处理完ui就刷新了  
  

使用  
1.build.gradle （根目录）  使用的jitpack库  
```java
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```
2.依赖 rxjava2 与rxandroid  
 compile 'io.reactivex.rxjava2:rxjava:2.0.1'  
 compile 'io.reactivex.rxjava2:rxandroid:2.0.1'  
 compile 'com.github.feteING:rxjava2asynctask:1.0.2'  
  

3.io运行  
```java
private void runIO() {
        Rxjava2.execute(new IOTask() {
            @Override
            public void doInIOThread() {
                for (int i = 0; i < 100; i++) {
                    Log.e("test_", i + "");
                }
                Log.e("_____", Thread.currentThread().getName());
            }
        });
    }

```

ui运行
```java
private void runUI() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Rxjava2.execute(new UITask() {
                    @Override
                    public void doInUIThread() {
                        Log.e("_____", Thread.currentThread().getName());
                        Toast.makeText(MainActivity.this, "disposable", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

    }

```

流式运行
```java
 String name = "";
    CompositeDisposable disposable;

    public void flowable() {

        disposable = Rxjava2.execute(new Task() {
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
                Log.e("disposable", name);
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                disposable.clear();
//            }
//        },1000);
    }

```

4.释放 disposable = rxjava2.excute()
```java
 @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.clear();
        }
    }

```

轮询执行  
(插入u盘，反应较慢，尝试3次，如果成功执行，失败抛出错误)
比如你想执行一个任务，执行3次每5秒执行poolIOUI(3,5,Task); io执行成功返回true，调用ui线程更新，如果没有执行成功一直返回false，不执行ui线程  
```java
 public void poolIOUI() {
        poolDisposable = Rxjava2.poolInIOUI(10, 3, new PoolIOUITask() {
            @Override
            public boolean preIO() {
                Random rand = new Random();
                int i = rand.nextInt(10);

                Log.e("random___", i + "");
                if (i == 5) {
                    Log.e("成功", i + "");
                    return true;
                }
                return false;
            }

            @Override
            public void updateUI() {
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
            }
        });


    }
```


