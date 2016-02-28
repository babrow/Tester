package com.babrow.tester.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.babrow.tester.R;
import com.babrow.tester.model.GameResult;
import com.babrow.tester.model.TrafficResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrafficActivity extends GameActivity {
    private static final int SECONDS_GAME = 60;
    private static final int MILLIS_TICK_INTERVAL = 100;
    private static final int MILLIS_TICK_INTERVAL_LIMIT = 2000;

    private ImageView trafficImg;
    private TRAFFIC_LIGHT currentLight;

    private long lastTickTime;
    private long lastActionTime;

    private long tickOverallInterval;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_traffic;
    }

    @Override
    public GameResult getGameResultImpl() {
        return new TrafficResult();
    }


    @Override
    public TrafficResult getGameResult() {
        return (TrafficResult) super.getGameResult();
    }

    @Override
    protected void onTick(long millisUntilFinished, int secondsLeft) {
        tickOverallInterval += MILLIS_TICK_INTERVAL;

        boolean doChangeLight = false;
        if (lastActionTime >= lastTickTime) {
            doChangeLight = true;
            lastActionTime = 0;
            tickOverallInterval = 0;
        }
        if (tickOverallInterval >= MILLIS_TICK_INTERVAL_LIMIT) {
            doChangeLight = true;
            tickOverallInterval = 0;
            if (currentLight != TRAFFIC_LIGHT.YELLOW) {
                getGameResult().addResult(new TrafficResult.TrafficResultRow(true, lastActionTime - lastTickTime));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trafficImg = (ImageView) findViewById(R.id.traffic_img);

        startTimer(SECONDS_GAME * GameActivity.MILLIS_PER_SECOND, MILLIS_TICK_INTERVAL);
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
        getGameResult().addResult(new TrafficResult.TrafficResultRow(isError, lastActionTime - lastTickTime));
    }

    private enum TRAFFIC_LIGHT {
        RED(R.drawable.traffic_red), YELLOW(R.drawable.traffic_yellow), GREEN(R.drawable.traffic_green);

        private static final List<TRAFFIC_LIGHT> VALUES =
                Collections.unmodifiableList(Arrays.asList(values()));
        private static final int SIZE = VALUES.size();
        private static final Random RANDOM = new Random();

        private int imageIndex;

        TRAFFIC_LIGHT(int imageIndex) {
            this.imageIndex = imageIndex;
        }

        public static TRAFFIC_LIGHT getRandomLight() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }

        public int getImageIndex() {
            return imageIndex;
        }
    }
}
