package com.babrow.tester;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Test2Activity extends AppCompatActivity {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 10;
    private static final int TICK_INTERVAL = MILLIS_PER_SECOND / 2;
    private static final int TICK_INTERVAL_LIMIT = MILLIS_PER_SECOND * 2;

    private TextView countdownDisplay;
    private ImageView trafficImg;
    private CountDownTimer timer;
    private TRAFFIC_LIGHT currentLight;

    private long lastTickTime;
    private long lastActionTime;

    private final Map<Boolean, List<Test2Result>> testResults = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);
        trafficImg = (ImageView) findViewById(R.id.traffic_img);

        startTimer(MILLIS_PER_SECOND * SECONDS_TO_COUNTDOWN, TICK_INTERVAL, TICK_INTERVAL_LIMIT);
    }

    public void drivingAction(View view) {
        lastActionTime = System.currentTimeMillis();

        boolean isError = false;
        int id = view.getId();
        switch (id) {
            case R.id.go_btn: {
                isError = currentLight != TRAFFIC_LIGHT.GREEN;
                break;
            }
            case R.id.stop_btn: {
                isError = currentLight != TRAFFIC_LIGHT.RED;
                break;
            }
        }
        addResult(isError, lastActionTime - lastTickTime);
    }

    private void startTimer(final int countdownMillis, final int tickInterval, final int tickIntervalLimit) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(countdownMillis, tickInterval) {
            int tickOverallInterval = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                tickOverallInterval += tickInterval;

                countdownDisplay.setText(Long.toString(millisUntilFinished / MILLIS_PER_SECOND));

                boolean doChangeLight = false;
                if (lastActionTime >= lastTickTime) {
                    doChangeLight = true;
                    lastActionTime = 0;
                    tickOverallInterval = 0;
                }
                if (tickOverallInterval >= tickIntervalLimit) {
                    doChangeLight = true;
                    tickOverallInterval = 0;
                    if (currentLight != TRAFFIC_LIGHT.YELLOW) {
                        addResult(true, lastActionTime - lastTickTime);
                    }
                }
                if (doChangeLight) {
                    lastTickTime = System.currentTimeMillis();

                    TRAFFIC_LIGHT tempCurrentLight = currentLight;
                    while (tempCurrentLight == currentLight) {
                        tempCurrentLight = TRAFFIC_LIGHT.getRandomLight();
                    }
                    currentLight = tempCurrentLight;
                    trafficImg.setImageResource(currentLight.getImageIndex());
                }
            }

            @Override
            public void onFinish() {
                showResults();
            }
        }.start();
    }

    private void addResult(boolean isError, long reaction) {
        List<Test2Result> res = testResults.get(isError);
        if (res == null) {
            res = new ArrayList<>();
            testResults.put(isError, res);
        }
        res.add(new Test2Result(isError, reaction));
    }

    private void showResults() {
        int errorsCnt = testResults.get(true) != null ? testResults.get(true).size() : 0;
        int posCnt = testResults.get(false) != null ? testResults.get(false).size() : 0;

        String str = "Результаты теста не достоверны";
        if (posCnt != 0) {
            long react = 0;
            for (Test2Result result : testResults.get(false)) {
                react += result.getReaction();
            }
            react = react / posCnt;
            str = "Процент ошибок: " + errorsCnt * 100/(errorsCnt + posCnt);
            str += "Среднее время реакции: " + react;
        }
        new AlertDialog.Builder(Test2Activity.this)
                .setTitle(R.string.test_results_title)
                .setMessage(str)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private enum TRAFFIC_LIGHT {
        RED(R.drawable.red), YELLOW(R.drawable.yellow), GREEN(R.drawable.green);

        private static final List<TRAFFIC_LIGHT> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        private int imageIndex;

        TRAFFIC_LIGHT(int imageIndex) {
            this.imageIndex = imageIndex;
        }

        public int getImageIndex() {
            return imageIndex;
        }

        public static TRAFFIC_LIGHT getRandomLight() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }

    private static class Test2Result {
        private boolean isError;
        private long reaction;

        public Test2Result(boolean isError, long reaction) {
            this.isError = isError;
            this.reaction = reaction;
        }

        public boolean isError() {
            return isError;
        }

        public long getReaction() {
            return reaction;
        }
    }
}
