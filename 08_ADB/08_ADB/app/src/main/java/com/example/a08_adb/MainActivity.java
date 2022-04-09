package com.example.a08_adb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private TextView ip = null;
    private TextView rx = null;
    private TextView tx = null;

    private Handler handler = null;
    private int updates = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = findViewById(R.id.ip);
        rx = findViewById(R.id.rx);
        tx = findViewById(R.id.tx);

        handler = new Handler();
        timeUpdate.run();

        setTitle("UPDATES: " + updates);
    }

    public void update() {
        ip.setText("IP:\t" + getIP());
        rx.setText("RX:\t" + getRx());
        tx.setText("TX:\t" + getTx());

        setTitle("UPDATES: " + updates++);
        Log.v("✅", "UPDATED!");
    }

    private String getIP() {
        try {
            Process p = Runtime.getRuntime().exec("ifconfig wlan0");
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String str = br.readLine();
            String[] strings = str.split("\\s+");
            String ip = strings[2];
            return ip;

        } catch (IOException e) {
            Log.v("❌", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return "ERROR";
    }

    private String getRx() {
        return getRxTx()[0];
    }

    private String getTx() {
        return getRxTx()[1];
    }

    private String[] getRxTx() {
        try {
            Process p = Runtime.getRuntime().exec("cat /proc/net/dev");
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String str = "";
            String[] result = new String[2];

            while ((str = br.readLine()) != null) {
                if (str.contains("wlan0")) {
                    String[] strings = str.split("\\s+");

                    String rx = strings[3];
                    result[0] = rx;

                    String tx = strings[11];
                    result[1] = tx;
                    return result;
                }
            }

        } catch (IOException e) {
            Log.v("❌", e.getLocalizedMessage());
            e.printStackTrace();
        }

        return new String[] { "", "" };
    }

    private Runnable timeUpdate = new Runnable() {
        @Override
        public void run() {
            update();
            handler.postDelayed(this, 1000);
        }
    };
}