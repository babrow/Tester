package com.babrow.tester.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.babrow.tester.App;
import com.babrow.tester.R;
import com.babrow.tester.model.Account;
import com.babrow.tester.model.GameResult;
import com.babrow.tester.model.TappingResult;
import com.babrow.tester.view.TappingView;

public class Test1Activity extends GameActivity implements AcceptsCallback {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_GAME = 30;
    private static final int SECONDS_RESULTS_INTERVAL = 5;
    private static final long MILLIS_TICK_INTERVAL = 100;

    private TextView countdownDisplay;
    private TappingView tappingView;

    private boolean isTimerRunning;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);

        tappingView = (TappingView) findViewById(R.id.tapping_zone);
        tappingView.setZOrderOnTop(true);
    }

    @Override
    public GameResult getGameResult() {
        if (result != null) {
            return result;
        }
        Account account = App.getAccount();
        if (account != null) {
            return new TappingResult(account.getId());
        }
        return null;
    }

    @Override
    protected void onStartTimer() {
        isTimerRunning = true;
    }

    @Override
    protected void onTick(long millisUntilFinished, int secondsLeft) {

    }

    @Override
    protected void onSecond(long millisUntilFinished, int secondsLeft) {
        countdownDisplay.setText(String.valueOf(secondsLeft));

        if (secondsLeft != SECONDS_GAME && secondsLeft % SECONDS_RESULTS_INTERVAL == 0) {
            result.addResult(tappingView.getTouchStats());
            tappingView.flushTouchStats();
        }
    }

    @Override
    protected void onFinish() {
        tappingView.setDisabledTouch(true);
    }

    @Override
    public void onMethodCallback() {
        if (!isTimerRunning) {
            startTimer(SECONDS_GAME * MILLIS_PER_SECOND, MILLIS_TICK_INTERVAL);
        }
    }
}
