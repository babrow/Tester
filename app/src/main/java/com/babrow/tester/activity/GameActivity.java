package com.babrow.tester.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.babrow.tester.App;
import com.babrow.tester.R;
import com.babrow.tester.model.GameResult;
import com.babrow.tester.utils.GameTimer;
import com.babrow.tester.utils.http.GenericRequest;

public abstract class GameActivity extends AppCompatActivity {
    protected static final int MILLIS_PER_SECOND = 1000;

    private TextView secondsLeftDisplay;

    private GameTimer timer;

    private GameResult gameResult;

    protected abstract int getLayoutId();

    public abstract GameResult getGameResultImpl();

    protected void onStartTimer() {

    }

    protected void onTick(long millisUntilFinished, int secondsLeft) {

    }

    protected void onSecond(long millisUntilFinished, int secondsLeft) {

    }

    protected void onFinish() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        secondsLeftDisplay = (TextView) findViewById(R.id.time_display_box);

        gameResult = getGameResultImpl();
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
                secondsLeftDisplay.setText(String.valueOf(secondsLeft));
                GameActivity.this.onSecond(millisUntilFinished, secondsLeft);
            }

            @Override
            public void onFinishHandler() {
                GameActivity.this.onFinish();
                showResults();
            }
        };
        timer.start();
    }

    public GameResult getGameResult() {
        return gameResult;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (timer != null) {
            timer.cancel();
        }
    }

    public void stopGame() {
        if (timer != null) {
            timer.cancel();
        }
        showResults();
    }

    public void showResults() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                .setMessage(getGameResult().toMessage())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .setPositiveButton(R.string.test_results_save, dlgActions)
                .setNegativeButton(R.string.test_results_rerun, dlgActions)
                .show();
    }

    public void saveResults() {
        GenericRequest<String> req = new GenericRequest<>(Request.Method.POST, App.SERVER_API.SAVE_RESULT_URL, String.class, getGameResult().toParams(),
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
