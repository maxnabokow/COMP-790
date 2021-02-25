package com.example.a02_bouncingballs;

import java.util.Random;

class Ball {

    private int x, y;
    private final int radius;
    Vector2D movementVector;

    private final Random random = new Random();

    public Ball(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;

        movementVector = new Vector2D(
                getRandomDirection() * 5,
                getRandomDirection() * 5
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

    void flipDirection() {
        movementVector.flipDirection();
    }

    void flipDirection(Direction direction) {
        movementVector.flipDirection(direction);
    }

    private int getRandomDirection() {
        return random.nextBoolean() ? 1 : -1;
    }

    // Collision Helpers

    boolean isCollidingWith(Ball other) {
        final boolean xCollide1 = this.getMinX() <= other.getMinX()
                && this.getMaxX() >= other.getMinX();

        final boolean xCollide2 = this.getMinX() <= other.getMaxX()
                && this.getMaxX() >= other.getMaxX();

        final boolean yCollide1 = this.getMinY() <= other.getMinY()
                && this.getMaxY() >= other.getMinY();

        final boolean yCollide2 = this.getMinY() <= other.getMaxY()
                && this.getMaxY() >= other.getMaxY();

        final boolean xCollide = xCollide1 || xCollide2;
        final boolean yCollide = yCollide1 || yCollide2;

        return xCollide && yCollide;
    }

    int getMinX() { return x - radius; }
    int getMaxX() { return x + radius; }
    int getMinY() { return y - radius; }
    int getMaxY() { return y + radius; }
}
