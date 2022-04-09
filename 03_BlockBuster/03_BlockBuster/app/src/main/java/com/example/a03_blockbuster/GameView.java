package com.example.a03_blockbuster;

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

public final class GameView extends View {

    MainActivity observer = null;

    private boolean ballMoving = false;

    private Ball ball = null;
    private List<Brick> bricks = new ArrayList<Brick>();
    private boolean[] bricksVisible = new boolean[10];
    private Bat bat = null;

    private final int WIDTH = 800;
    private final int HEIGHT = 1000;
    private final int BALL_RADIUS = 15;

    private final int BRICK_WIDTH = 100;
    private final int BRICK_HEIGHT = 35;

    private final int BAT_WIDTH = 200;
    private final int BAT_HEIGHT = 25;

    private final Random random = new Random();

    // Constructors

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        initBricks();
        initBall();
        initBat();
        invalidate();
    }

    // Methods

    private void initBricks() {
        bricks.clear();

        final int baseX = 50;
        final int baseY = 50;
        final int xSpacing = 50;
        final int ySpacing = 50;

        for (int yCount = 0; yCount < 2; yCount++) {
            final int y = baseY + (ySpacing * yCount);

            for (int xCount = 0; xCount < 5; xCount++) {
                final int x = baseX + ((BRICK_WIDTH + xSpacing) * xCount);
                final Brick brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                bricks.add(brick);
            }
        }

        for (int i = 0; i < 10; i++) {
            bricksVisible[i] = true;
        }
    }

    private void initBall() {
        final int centerX = WIDTH / 2;
        final int centerY = HEIGHT / 2;

        ball = new Ball(centerX, centerY, BALL_RADIUS);
    }

    private void initBat() {
        final int centerX = (WIDTH - BAT_WIDTH) / 2;
        final int bottomY = HEIGHT - BAT_HEIGHT;

        bat = new Bat(centerX, bottomY, BAT_WIDTH, BAT_HEIGHT);
    }

    public void updateBat(float percentOfWidth) {
        final int maxX = 800-BAT_WIDTH;
        final int x = (int) (percentOfWidth * maxX);
        final int bottomY = HEIGHT - BAT_HEIGHT;
        bat = new Bat(x, bottomY, BAT_WIDTH, BAT_HEIGHT);
        invalidate();
    }

    public void startMovingBall() {
        ballMoving = true;
    }
    public void stopMovingBall() {
        ballMoving = false;
    }

    public boolean gameWon() {
        for (boolean b : bricksVisible) {
            if (b) { return false; }
        }
        return true;
    }

    public boolean gameLost() {
        return ball.getMinY() > HEIGHT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawField(canvas);

        // move balls
        if (ballMoving) {
            ball.move();
        }
        drawBall(ball, canvas);

        if (ball.getMaxX() >= WIDTH || ball.getMinX() <= 0)
            ball.flipDirection(Direction.X);

        if (ball.getMinY() <= 0)
            ball.flipDirection(Direction.Y);

        if (ball.getMinY() > HEIGHT) {
            observer.loseGame();
        }

        if (ballBatCollision())
            ball.flipDirection(Direction.Y);

        for (int i = 0; i < bricks.size(); i++) {
            if (bricksVisible[i]) {
                final Brick brick = bricks.get(i);
                drawBrick(canvas, brick);
                final int collisionNumber = ballBrickCollision(brick);
                if (collisionNumber == 0) {
                    ball.flipDirection(Direction.Y);
                    bricksVisible[i] = false;
                } else if (collisionNumber == 1) {
                    ball.flipDirection(Direction.X);
                    bricksVisible[i] = false;
                }
            }
        }

        drawBat(canvas);

        // redraw to create continuous animation
        invalidate();
    }

    // Collision Helpers

    private boolean ballBatCollision() {
        final boolean xOverlap1 =
                ball.getMinX() <= bat.getRect().left
                        && ball.getMaxX() >= bat.getRect().left;
        final boolean xOverlap2 =
                ball.getMinX() >= bat.getRect().left
                        && ball.getMaxX() <= bat.getRect().right;
        final boolean xOverlap3 =
                ball.getMinX() <= bat.getRect().right
                        && ball.getMaxX() >= bat.getRect().right;

        final boolean xOverlap = xOverlap1 || xOverlap2 || xOverlap3;
        final boolean yOverlap = ball.getMaxY() > bat.getRect().top;

        return xOverlap && yOverlap;
    }

    private int ballBrickCollision(Brick brick) {
        final boolean xOverlap1 =
                ball.getMinX() <= brick.getRect().left
                        && ball.getMaxX() >= brick.getRect().left;
        final boolean xOverlap2 =
                ball.getMinX() >= brick.getRect().left
                        && ball.getMaxX() <= brick.getRect().right;
        final boolean xOverlap3 =
                ball.getMinX() <= brick.getRect().right
                        && ball.getMaxX() >= brick.getRect().right;

        final boolean xOverlap = xOverlap1 || xOverlap2 || xOverlap3;

        final boolean yOverlap1 =
                ball.getMinY() <= brick.getRect().bottom
                        && ball.getMaxY() >= brick.getRect().bottom;
        final boolean yOverlap2 =
                ball.getMinY() >= brick.getRect().top
                        && ball.getMaxY() <= brick.getRect().bottom;
        final boolean yOverlap3 =
                ball.getMinY() <= brick.getRect().top
                        && ball.getMaxY() >= brick.getRect().top;

        final boolean yOverlap = yOverlap1 || yOverlap2 || yOverlap3;

        if (xOverlap2 && yOverlap) {
            return 0;
        } else if (yOverlap2 && xOverlap) {
            return 1;
        }

        return -1;
    }

    // Drawing Helpers

    private void drawField(Canvas canvas) {
        final Paint black = new Paint(Color.BLACK);

        canvas.drawLine(0, 0, WIDTH, 0, black);
        canvas.drawLine(WIDTH, 0, WIDTH, HEIGHT, black);
        canvas.drawLine(WIDTH, HEIGHT, 0, HEIGHT, black);
        canvas.drawLine(0, HEIGHT, 0, 0, black);
    }

    private void drawBrick(Canvas canvas, Brick brick) {
        final Paint black = new Paint(Color.BLACK);

        canvas.drawRect(brick.getRect(), black);
    }

    private void drawBat(Canvas canvas) {
        final Paint black = new Paint(Color.BLACK);

        canvas.drawRect(bat.getRect(), black);
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
