package com.babrow.tester.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by babrow on 16.01.2016.
 */
public class DrawingView extends SurfaceView {

    private SurfaceHolder surfaceHolder;
    private final static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final static int canvasColor = Color.WHITE;
    private volatile int touchCounter = 0;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        surfaceHolder = getHolder();
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            drawCircle(event.getX(), event.getY());
            addTouch();
        }
        return false;
    }

    private void drawCircle(float x, float y) {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(canvasColor);
            TouchCircle circle = TouchCircle.getRandInstance();
            paint.setColor(circle.getColor());
            paint.setStrokeWidth(circle.getWidth());
            canvas.drawCircle(x, y, circle.getRadius(), paint);

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public int getTouchStats() {
        return touchCounter;
    }

    private synchronized void addTouch() {
        touchCounter++;
    }

    public synchronized void flushTouchStats() {
        touchCounter = 0;
    }

    private static class TouchCircle {
        private static final int[] colors = {Color.BLUE, Color.GREEN, Color.RED};
        private Random random = new Random();
        private int color;
        private int width;
        private int radius;

        public int getColor() {
            return color;
        }

        public int getWidth() {
            return width;
        }

        public int getRadius() {
            return radius;
        }

        private TouchCircle() {
            this.color = getRandomColor();
            this.radius = random.nextInt(70 - 30 + 1) + 30;
            this.width = random.nextInt(10 - 1 + 1) + 1;
        }

        public static TouchCircle getRandInstance() {
            return new TouchCircle();
        }

        private int getRandomColor() {
            int i = random.nextInt(colors.length);
            return colors[i];
        }

    }
}
