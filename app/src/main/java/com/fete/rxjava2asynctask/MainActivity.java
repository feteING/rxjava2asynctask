package com.fete.rxjava2asynctask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fete.rxjava2task.IOTask;
import com.fete.rxjava2task.Rxjava2;
import com.fete.rxjava2task.Task;
import com.fete.rxjava2task.UITask;

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
//                int i = 1/0;
                //                setT(new TestModel("yhf"));
            }

            @Override
            public void updateUI() {
//                TestModel t = (TestModel) getT();
                Log.e("disposable", name);
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
//                int i = 1/0;
            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                disposable.clear();
//            }
//        },1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.clear();
        }
    }
}
