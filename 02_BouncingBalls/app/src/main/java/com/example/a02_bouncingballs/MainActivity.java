package com.example.a02_bouncingballs;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BallsView ballsView;
    private TextView collisionCountTextView;
    private TextView ballCountTextView;
    private TextView timeElapsedTextView;

    private Handler handler;
    private int seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ballsView = findViewById(R.id.ballsView);
        ballsView.observer = this;

        collisionCountTextView = findViewById(R.id.collisionCount);
        updateCollisionCount(0);

        ballCountTextView = findViewById(R.id.ballCount);
        updateBallCount(0);

        timeElapsedTextView = findViewById(R.id.time);
        updateTimeElapsed(0);

        handler = new Handler();
    }

    public void updateBallCount(int numBalls) {
        ballCountTextView.setText("Number of Balls: " + numBalls);
    }

    public void updateCollisionCount(int numCollisions) {
        collisionCountTextView.setText("Number of Collisions: " + numCollisions);
    }

    private void updateTimeElapsed(int seconds) {
        timeElapsedTextView.setText("Time Elapsed: " + seconds + "s");
    }

    public void addBall(View view) {
        if (handler.hasCallbacks(timeUpdate)) {
            handler.removeCallbacks(timeUpdate);
        }
        startTimer();
        updateTimeElapsed(0);
        updateCollisionCount(0);
        ballsView.addBall();
    }

    private void startTimer() {
        seconds = 0;
        handler.post(timeUpdate);
    }

    Runnable timeUpdate = new Runnable() {
        @Override
        public void run() {
            seconds++;
            updateTimeElapsed(seconds);

            handler.postDelayed(this,1000);
        }
    };
}
