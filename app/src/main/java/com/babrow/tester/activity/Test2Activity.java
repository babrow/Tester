package com.babrow.tester.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.babrow.tester.R;
import com.babrow.tester.model.Account;
import com.babrow.tester.model.TrafficResult;
import com.babrow.tester.utils.http.GenericRequest;
import com.babrow.tester.utils.http.RequestSender;
import com.babrow.tester.utils.http.Settings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Test2Activity extends AppCompatActivity {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 60;
    private static final int TICK_INTERVAL = MILLIS_PER_SECOND / 2;
    private static final int TICK_INTERVAL_LIMIT = MILLIS_PER_SECOND * 2;

    private Account account;

    private TextView countdownDisplay;
    private ImageView trafficImg;
    private CountDownTimer timer;
    private TRAFFIC_LIGHT currentLight;

    private long lastTickTime;
    private long lastActionTime;

    private TrafficResult result;

    private RequestQueue reqQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);
        trafficImg = (ImageView) findViewById(R.id.traffic_img);

        account = (Account) getIntent().getSerializableExtra(Settings.ACCOUNT);
        result = new TrafficResult(account.getId());

        reqQueue = Volley.newRequestQueue(this);

        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();

        timer.cancel();
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
        result.addResult(new TrafficResult.TrafficResultRow(isError, lastActionTime - lastTickTime));
    }

    private void startTimer() {
        result.flush();
        startTimer(MILLIS_PER_SECOND * SECONDS_TO_COUNTDOWN, TICK_INTERVAL, TICK_INTERVAL_LIMIT);
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
                        result.addResult(new TrafficResult.TrafficResultRow(true, lastActionTime - lastTickTime));
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

    private void showResults() {
        DialogInterface.OnClickListener dlgActions = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        saveResults();
                        finish();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        startTimer();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.test_results_title)
                .setMessage(result.toMessage())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.test_results_save, dlgActions)
                .setNegativeButton(R.string.test_results_rerun, dlgActions)
                .show();
    }

    public void saveResults() {
        GenericRequest<String> req = new GenericRequest<>(Request.Method.POST, RequestSender.SAVE_RESULT_URL, String.class, result.toParams(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String answer) {

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        reqQueue.add(req);
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

        public int getImageIndex() {
            return imageIndex;
        }

        public static TRAFFIC_LIGHT getRandomLight() {
            return VALUES.get(RANDOM.nextInt(SIZE));
        }
    }
}
