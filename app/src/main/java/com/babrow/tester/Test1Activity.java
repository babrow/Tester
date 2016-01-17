package com.babrow.tester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.babrow.tester.customview.DrawingView;

import java.util.ArrayList;
import java.util.List;

public class Test1Activity extends AppCompatActivity {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 30;
    private static final int SECONDS_RESULTS_INTERVAL = 5;

    private TextView countdownDisplay;
    private DrawingView tappingZone;
    private CountDownTimer timer;
    private DialogInterface.OnClickListener dlgActions;
    private List<Integer> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);
        tappingZone = (DrawingView) findViewById(R.id.tapping_zone);
        initDlgActions();

        startTimer();
    }

    private void startTimer() {
        startTimer(SECONDS_TO_COUNTDOWN * MILLIS_PER_SECOND, MILLIS_PER_SECOND, SECONDS_RESULTS_INTERVAL);
    }

    private void startTimer(final int countdownMillis, final int millisTickInterval, final int secondsResultsInterval) {
        results = new ArrayList<>();

        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(countdownMillis, millisTickInterval) {
            private int tickCounter = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                tickCounter++;
                countdownDisplay.setText(Long.toString(millisUntilFinished / MILLIS_PER_SECOND));

                if (tickCounter % secondsResultsInterval == 0) {
                    results.add(tappingZone.getTouchStats());
                    tappingZone.flushTouchStats();
                }
            }

            @Override
            public void onFinish() {
                showResults();
            }
        }.start();
    }

    private void showResults() {
        new AlertDialog.Builder(Test1Activity.this)
                .setTitle(R.string.test_results_title)
                .setMessage(results.toString())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.test_results_save, dlgActions)
                .setNegativeButton(R.string.test_results_rerun, dlgActions)
                .show();
    }

    private void initDlgActions() {
        dlgActions = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        startTimer();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        startTimer();
                        break;
                }
            }
        };
    }


}
