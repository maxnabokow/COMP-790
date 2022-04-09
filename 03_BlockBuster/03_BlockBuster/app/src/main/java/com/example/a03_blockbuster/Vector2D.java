package com.example.a03_blockbuster;

public final class Vector2D {
    int dx, dy;

    public Vector2D(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    void flipDirection() {
        dx = -dx;
        dy = -dy;
    }

    void flipDirection(Direction direction) {
        switch (direction) {
            case X: dx = -dx; break;
            case Y: dy = -dy; break;
        }
    }

}

enum Direction {
    X, Y
}