package com.example.a03_blockbuster;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private GameView gameView;
    private TextView statusTextView;
    private TextView instructionTextView;

    private SensorManager sensorManager;
    private Sensor sensor;

    private Handler handler;
    private float seconds = 0;
    private boolean gameRunning = false;

    // shake
    private long lastUpdate = 0;
    private float last_x = 0;
    private float last_y = 0;
    private float last_z = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.ballsView);
        statusTextView = findViewById(R.id.status);
        gameView.observer = this;
        handler = new Handler();

        // set up sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void startGame(View view) {
        if (handler.hasCallbacks(timeUpdate)) {
            handler.removeCallbacks(timeUpdate);
        }
        gameRunning = true;
        startTimer();
        gameView.startMovingBall();
    }

    private void resetBoard() {
        gameView.init();
    }

    private void updateTimeElapsed() {
        statusTextView.setText(" Time Elapsed: " + (int) seconds + "s");
    }

    private void startTimer() {
        seconds = 0;
        timeUpdate.run();
    }

    private void winGame() {
        statusTextView.setText(" Game won in " + (int) seconds + " seconds! \n Shake to restart.");
        gameRunning = false;
        gameView.stopMovingBall();
    }

    public void loseGame() {
        statusTextView.setText(" Game lost! Played for " + (int) seconds + " seconds.\n Shake to restart.");
        gameRunning = false;
        gameView.stopMovingBall();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) { return; };

        if (gameRunning) {
            float x = -event.values[0];
            x += 3.5;
            float adjustedX = Math.max(0, x);
            float percentX = Math.min(adjustedX / 7, 1);
            gameView.updateBat(percentX);
        } else {
            final int SHAKE_THRESHOLD = 1200;
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    resetBoard();
                    Toast.makeText(this, "Game Started!", Toast.LENGTH_SHORT).show();
                    startGame(null);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private Runnable timeUpdate = new Runnable() {
        @Override
        public void run() {
            if (gameView.gameWon()) {
                winGame();
            } else if (!gameView.gameLost()) {
                updateTimeElapsed();
                seconds += 0.1;

                handler.postDelayed(this, 100);
            }
        }
    };
}