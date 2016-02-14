package com.babrow.tester.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.babrow.tester.App;
import com.babrow.tester.R;
import com.babrow.tester.model.GameResult;
import com.babrow.tester.utils.GameTimer;
import com.babrow.tester.utils.http.GenericRequest;
import com.babrow.tester.utils.http.RequestSender;

/**
 * Created by babrow on 14.02.2016.
 */
public abstract class GameActivity extends AppCompatActivity {
    private GameTimer timer;
    protected GameResult result;

    protected abstract int getLayoutId();

    public abstract GameResult getGameResult();

    protected abstract void onStartTimer();

    protected abstract void onTick(long millisUntilFinished, int secondsLeft);

    protected abstract void onSecond(long millisUntilFinished, int secondsLeft);

    protected abstract void onFinish();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        result = getGameResult();
    }

    public void startTimer(long gameMillis, long tickMillis) {
        onStartTimer();

        timer = new GameTimer(gameMillis, tickMillis) {
            @Override
            public void onTickHandler(long millisUntilFinished, int secondsLeft) {
                GameActivity.this.onTick(millisUntilFinished, secondsLeft);
            }

            @Override
            public void onSecondHandler(long millisUntilFinished, int secondsLeft) {
                GameActivity.this.onSecond(millisUntilFinished, secondsLeft);
            }

            @Override
            public void onFinishHandler() {
                GameActivity.this.onFinish();
                showResults();
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (timer != null) {
            timer.cancel();
        }
    }

    public void showResults() {
        DialogInterface.OnClickListener dlgActions = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        saveResults();
                        finish();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        startActivity(getIntent());
                        finish();
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

        App.sendRequest(req);
    }
}
