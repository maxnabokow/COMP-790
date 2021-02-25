package com.example.a02_bouncingballs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BallsView extends View {

    MainActivity observer = null;

    private final List<Ball> balls;

    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int BALL_RADIUS = 15;

    public int numCollisions;
    private final Random random;

    // Constructors

    public BallsView(Context context) {
        super(context);
        balls = new ArrayList<Ball>();
        numCollisions = 0;
        random = new Random();
    }
    public BallsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        balls = new ArrayList<Ball>();
        numCollisions = 0;
        random = new Random();
    }
    public BallsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        balls = new ArrayList<Ball>();
        numCollisions = 0;
        random = new Random();
    }

    // Methods

    public int addBall() {
        final int centerX = WIDTH / 2;
        final int centerY = HEIGHT / 2;
        final int x = centerX + getRandomOffset();
        final int y = centerY + getRandomOffset();

        final Ball ball = new Ball(x, y, BALL_RADIUS);
        balls.add(ball);

        invalidate();

        final int numBalls = balls.size();
        observer.updateBallCount(numBalls);

        numCollisions = 0;

        return numBalls;
    }

    private int getRandomOffset() {
        return getRandomDirection() * random.nextInt(25);
    }

    private int getRandomDirection() {
        return random.nextBoolean() ? 1 : -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawField(canvas);

        // move balls
        for (int i = 0; i < balls.size(); i++) {
            final Ball ball = balls.get(i);
            ball.move();
            drawBall(ball, canvas);

            if (ball.getMaxX() >= WIDTH || ball.getMinX() <= 0) ball.flipDirection(Direction.X);
            if (ball.getMaxY() >= HEIGHT || ball.getMinY() <= 0) ball.flipDirection(Direction.Y);
        }

        // check for collisions
        if (balls.size() > 1) {
            for (int i = 0; i < balls.size()-1; i++) {
                final Ball first = balls.get(i);

                for (int j = i + 1; j < balls.size(); j++) {

                    final Ball second = balls.get(j);

                    if (first.isCollidingWith(second)) {
                        first.flipDirection();
                        second.flipDirection();

                        numCollisions++;
                        observer.updateCollisionCount(numCollisions);
                    }
                }
            }
        }

        // redraw to create continuous animation
        invalidate();
    }

    private void drawField(Canvas canvas) {
        final Paint black = new Paint(Color.BLACK);

        canvas.drawLine(0, 0, WIDTH, 0, black);
        canvas.drawLine(WIDTH, 0, WIDTH, HEIGHT, black);
        canvas.drawLine(WIDTH, HEIGHT, 0, HEIGHT, black);
        canvas.drawLine(0, HEIGHT, 0, 0, black);
    }

    private void drawBall(Ball ball, Canvas canvas) {
        final Paint paint = getRandomPaint();
        final int x = ball.getX();
        final int y = ball.getY();
        final int radius = ball.getRadius();

        canvas.drawCircle(x, y, radius, paint);
    }

    private Paint getRandomPaint() {
        int[] colors = { Color.BLUE, Color.RED, Color.GREEN };
        int index = random.nextInt(2);
        return new Paint(colors[index]);
    }
}