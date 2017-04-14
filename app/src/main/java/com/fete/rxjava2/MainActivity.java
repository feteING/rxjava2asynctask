package com.fete.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fete.rxjava2asynctask.IOTask;
import com.fete.rxjava2asynctask.PoolIOUITask;
import com.fete.rxjava2asynctask.PoolTask;
import com.fete.rxjava2asynctask.Rxjava2;
import com.fete.rxjava2asynctask.Task;
import com.fete.rxjava2asynctask.UITask;

import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runIO();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runUI();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flowable();
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poolUI();
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelPool();
            }
        });
        findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poolIO();
            }
        });
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                poolIOUI();
            }
        });
    }


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

    CompositeDisposable poolDisposable;

    public void poolUI() {

        poolDisposable = Rxjava2.poolInUi(10, 3, new PoolTask() {


            @Override
            public boolean execute() {
                Random rand = new Random();
                int i = rand.nextInt(10);

//                for (long k = 0; k < 1000000000L; k++) {
//
//                }
                Log.e("random___", i + "");


                if (i == 5) {
                    Log.e("成功", i + "");
                    Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }

        });

//        Toast.makeText(MainActivity.this, "接着往下执行", Toast.LENGTH_LONG).show();

    }

    public void cancelPool() {
        Rxjava2.cancelPool();

    }

    public void poolIO() {
        poolDisposable = Rxjava2.poolInIo(10, 3, new PoolTask() {


            @Override
            public boolean execute() {
                Random rand = new Random();
                int i = rand.nextInt(10);

//                for (long k = 0; k < 1000000000L; k++) {
//
//                }
                Log.e("random___", i + "");
                if (i == 5) {
                    Log.e("成功", i + "");
                    return true;
                }
                return false;
            }
        });


    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (poolDisposable != null) {
            poolDisposable.clear();
        }
    }
}
