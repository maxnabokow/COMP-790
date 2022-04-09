package com.example.a04_keepingtheappresponsive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int numSeconds = 1;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
    }

    public void clicked(View view) {
        new LongTask().execute();
        numSeconds++;
    }

    private class LongTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... strings) {
            try {
                for (int i = 0; i < numSeconds; i++) {
                    Thread.sleep(1000);
                    if (i == numSeconds - 1) {
                        return i + " seconds elapsed";
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String string) {
            textView.setText(string);
        }
    }

    /* THREAD-BASED SOLUTION below

    public void clicked(View view) {
        new Thread(new Runnable() {
            public void run() {
                foo(numSeconds);
            }
        }).start();
        numSeconds++;
    }

    private void foo(int  m) {
        try {
            for (int i = 0; i < m; i++) {
                Thread.sleep(1000);
                textView.setText(i + " seconds elapsed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

     */
}