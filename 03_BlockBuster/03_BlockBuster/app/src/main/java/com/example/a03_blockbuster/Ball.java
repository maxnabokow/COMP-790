package com.example.a03_blockbuster;

import java.util.Random;

public final class Ball {

    private int x, y;
    private final int radius;
    Vector2D movementVector;

    private final Random random = new Random();

    public Ball(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;

        movementVector = new Vector2D(
                getRandomDirection() * 10,
                getRandomDirection() * 10
        );
    }

    // Public Getters
    int getX() { return x; }
    int getY() { return y; }
    int getRadius() { return radius; }

    // Methods

    void move() {
        x += movementVector.dx;
        y += movementVector.dy;
    }

    void flipDirection(Direction direction) {
        movementVector.flipDirection(direction);
    }

    private int getRandomDirection() {
        return random.nextBoolean() ? 1 : -1;
    }

    int getMinX() { return x - radius; }
    int getMaxX() { return x + radius; }
    int getMinY() { return y - radius; }
    int getMaxY() { return y + radius; }
}
