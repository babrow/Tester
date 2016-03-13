package com.babrow.tester.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babrow.tester.App;
import com.babrow.tester.R;
import com.babrow.tester.model.GameResult;
import com.babrow.tester.model.MemoryResult;
import com.babrow.tester.utils.GameTimer;
import com.babrow.tester.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryActivity extends GameActivity {
    private static final long MILLIS_TICK_INTERVAL = 100;

    private static final int NUMBER_CNT_INIT = 4;
    private static final int NUMBER_CNT_INCREMENT = 2;
    private static final int NUMBER_CNT_LIMIT = 12;

    private static final int ITERATION_CNT_LIMIT = 5;

    private int numberCnt = NUMBER_CNT_INIT;
    private int iterationCnt = 1;

    private TextView mNumberView;
    private RelativeLayout mInputButtonLayout;
    private LinearLayout mInputLayout;
    private Button mButton;
    private List<Integer> viewedNumbers;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_memory;
    }

    @Override
    public MemoryResult getGameResult() {
        return (MemoryResult) super.getGameResult();
    }

    @Override
    public GameResult getGameResultImpl() {
        return new MemoryResult();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNumberView = (TextView) findViewById(R.id.number_view);
        mInputButtonLayout = (RelativeLayout) findViewById(R.id.input_button_layout);
        mInputLayout = (LinearLayout) findViewById(R.id.input_layout);
        mButton = (Button) findViewById(R.id.button_view);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                } catch (Exception e) {
                }

                inputSums();
            }
        });

        startGame();
    }

    private void inputSums() {
        List<Integer> inputSums = getInputSums();
        if (inputSums == null) {
            Toast toast = Toast.makeText(this, App.getContext().getResources().getString(R.string.test4_wrong_input_sums),
                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            getGameResult().addResult(new MemoryResult.MemoryResultRow(viewedNumbers,
                    inputSums, iterationCnt));

            if (numberCnt < NUMBER_CNT_LIMIT) {
                numberCnt += NUMBER_CNT_INCREMENT;
            } else {
                numberCnt = NUMBER_CNT_INIT;
                iterationCnt++;
            }
            if (iterationCnt == ITERATION_CNT_LIMIT) {
                showResults();
            } else {
                startGame();
            }
        }
    }

    private void startGame() {
        viewedNumbers = generateRandomSequence(numberCnt);

        final List<Integer> locNumbers = new ArrayList<>(viewedNumbers);
        new GameTimer(numberCnt * GameActivity.MILLIS_PER_SECOND * 2, MILLIS_TICK_INTERVAL) {
            int i = 0;

            @Override
            public void onCreate() {
                setViewMode(true);
            }

            @Override
            public void onSecondHandler(long millisUntilFinished, int secondsLeft) {
                if (secondsLeft % 2 == 0) {
                    mNumberView.setText("");
                } else {
                    mNumberView.setText(String.valueOf(locNumbers.get(i)));
                    i++;
                }
            }

            @Override
            public void onFinishHandler() {
                setViewMode(false);
            }
        }.start();

    }

    private List<Integer> generateRandomSequence(int seqLength) {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= seqLength / 2; i++) {
            result.addAll(generateRandomPair());
        }
        return result;
    }

    private List<Integer> generateRandomPair() {
        Random rand = new Random();
        int a = 0;
        int b = 0;
        while (a + b == 0 || a + b > 9) {
            a = Utils.generateRandomInt(rand, 1, 9);
            b = Utils.generateRandomInt(rand, 1, 9);
        }
        List<Integer> result = new ArrayList<>();
        result.add(a);
        result.add(b);
        return result;
    }

    private void setViewMode(boolean viewMode) {
        if (viewMode) {
            mInputButtonLayout.setVisibility(View.GONE);
            mNumberView.setText("");
            mNumberView.setVisibility(View.VISIBLE);
        } else {
            mInputButtonLayout.setVisibility(View.VISIBLE);
            mNumberView.setVisibility(View.GONE);

            refreshInputs();
        }
    }

    private void refreshInputs() {
        mInputLayout.removeAllViews();

        for (int i = 0; i < numberCnt / 2; i++) {
            EditText mInput = new EditText(MemoryActivity.this);
            mInput.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1));
            mInput.setInputType(EditorInfo.TYPE_CLASS_PHONE);

            mInputLayout.addView(mInput);
        }
        ;
    }

    private List<Integer> getInputSums() {
        List<Integer> inputsData = new ArrayList<>();
        for (int i = 0; i < mInputLayout.getChildCount(); i++) {
            EditText input = (EditText) mInputLayout.getChildAt(i);
            try {
                Integer number = Integer.valueOf(input.getText().toString());
                inputsData.add(number);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return inputsData;
    }
}