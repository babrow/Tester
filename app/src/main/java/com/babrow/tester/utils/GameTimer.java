package com.babrow.tester.utils;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by babrow on 14.02.2016.
 */
public class GameTimer extends CountDownTimer {
    private static final float MILLIS_PER_SECOND = 1000.0f;
    private int secondsLeft = 0;

    public GameTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);

        onCreate();
    }

    public void onCreate() {

    }

    public void onTickHandler(long millisUntilFinished, int secondsLeft) {

    }

    public void onSecondHandler(long millisUntilFinished, int secondsLeft) {

    }

    public void onFinishHandler() {

    }

    @Override
    public void onTick(long millisUntilFinished) {
        onTickHandler(millisUntilFinished, secondsLeft);
        Log.d("GameTimer.onTick", "secondsLeft=" + String.valueOf(secondsLeft) + ", millisUntilFinished=" + millisUntilFinished);
        if (Math.round((float) millisUntilFinished / MILLIS_PER_SECOND) != secondsLeft) {
            secondsLeft = Math.round((float) millisUntilFinished / MILLIS_PER_SECOND);
            Log.d("GameTimer.onSecond", "secondsLeft=" + String.valueOf(secondsLeft) + ", millisUntilFinished=" + millisUntilFinished);
            onSecondHandler(millisUntilFinished, secondsLeft);
        }
    }

    @Override
    public void onFinish() {
        Log.d("GameTimer.onFinish", "");
        onFinishHandler();
    }
}
