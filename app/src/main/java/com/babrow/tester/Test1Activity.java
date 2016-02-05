package com.babrow.tester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.babrow.tester.model.Account;
import com.babrow.tester.utils.http.GenericRequest;
import com.babrow.tester.utils.http.RequestSender;
import com.babrow.tester.utils.http.Settings;
import com.babrow.tester.view.DrawingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test1Activity extends AppCompatActivity {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 30;
    private static final int SECONDS_RESULTS_INTERVAL = 5;

    private TextView countdownDisplay;
    private DrawingView tappingZone;
    private CountDownTimer timer;
    private List<Integer> results;

    private RequestQueue reqQueue;
    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);
        tappingZone = (DrawingView) findViewById(R.id.tapping_zone);
        tappingZone.setZOrderOnTop(true);

        reqQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra(Settings.ACCOUNT);

        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();

        timer.cancel();
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

        String resStr = results.toString().replaceAll("\\[|\\]", "");
        resStr = String.format(getResources().getString(R.string.test1_results_description), resStr);
        new AlertDialog.Builder(this)
                .setTitle(R.string.test_results_title)
                .setMessage(resStr)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.test_results_save, dlgActions)
                .setNegativeButton(R.string.test_results_rerun, dlgActions)
                .show();
    }

    public void saveResults() {
        Map<String, String> data = new HashMap<>();
        data.put("testId", String.valueOf(1));
        data.put("accountId,", String.valueOf(account.getId()));
        for (int i = 0; i < results.size(); i++) {
            data.put(String.format(getResources().getString(R.string.test1_results_save), i), String.valueOf(results.get(i)));
        }
        GenericRequest<String> req = new GenericRequest<>(Request.Method.POST, RequestSender.SAVE_RESULT_URL, String.class, data,
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
}
