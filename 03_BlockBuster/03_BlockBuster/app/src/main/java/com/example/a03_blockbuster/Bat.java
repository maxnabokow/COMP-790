package com.example.a03_blockbuster;

import android.graphics.RectF;

public final class Bat {

    private int x, y;
    private int width, height;

    public Bat(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public RectF getRect() {
        final int maxX = x + width;
        final int maxY = y + height;
        return new RectF(x, y, maxX, maxY);
    }
}
