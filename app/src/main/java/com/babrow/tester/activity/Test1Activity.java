package com.babrow.tester.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.babrow.tester.R;
import com.babrow.tester.model.Account;
import com.babrow.tester.model.TappingResult;
import com.babrow.tester.utils.http.GenericRequest;
import com.babrow.tester.utils.http.RequestSender;
import com.babrow.tester.utils.http.Settings;
import com.babrow.tester.view.TappingView;

import java.math.BigDecimal;

public class Test1Activity extends AppCompatActivity implements AcceptsCallback {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_TO_COUNTDOWN = 30;
    private static final int SECONDS_RESULTS_INTERVAL = 5;

    private TextView countdownDisplay;
    private TappingView tappingView;
    private CountDownTimer timer;
    private TappingResult result;

    private RequestQueue reqQueue;
    private Account account;
    private boolean isTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        countdownDisplay = (TextView) findViewById(R.id.time_display_box);

        tappingView = (TappingView) findViewById(R.id.tapping_zone);
        tappingView.setZOrderOnTop(true);

        reqQueue = Volley.newRequestQueue(this);

        account = (Account) getIntent().getSerializableExtra(Settings.ACCOUNT);
        result = new TappingResult(account.getId());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void startTimer() {
        result.flush();
        tappingView.flushTouchStats();
        startTimer(SECONDS_TO_COUNTDOWN * MILLIS_PER_SECOND, MILLIS_PER_SECOND, SECONDS_RESULTS_INTERVAL);
    }

    private void startTimer(final int countdownMillis, final int millisTickInterval, final int secondsResultsInterval) {
        isTimerRunning = true;

        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(countdownMillis, 100) {
            int secondsLeft = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);

                    countdownDisplay.setText(String.valueOf(secondsLeft));

                    if (secondsLeft != SECONDS_TO_COUNTDOWN && secondsLeft % secondsResultsInterval == 0) {
                        Log.d("tick", String.valueOf(secondsLeft));
                        result.addResult(tappingView.getTouchStats());
                        tappingView.flushTouchStats();
                    }
                }

            }

            @Override
            public void onFinish() {
                tappingView.setDisabledTouch(true);
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
                        Intent intent = getIntent();
                        intent.putExtra(Settings.ACCOUNT, account);
                        finish();
                        startActivity(intent);
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

        req.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqQueue.add(req);
    }

    @Override
    public void onMethodCallback() {
        if (!isTimerRunning) {
            startTimer();
        }
    }
}
