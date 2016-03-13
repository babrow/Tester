package com.babrow.tester.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.babrow.tester.R;
import com.babrow.tester.model.GameResult;
import com.babrow.tester.model.RamResult;
import com.babrow.tester.utils.Utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RAMActivity extends GameActivity {
    private static final int NUMBERS_COUNT = 25;
    private static final int MAX_NUMBER = 99;
    private static final int MIN_NUMBER = 1;
    private static final int SECONDS_GAME = 120;
    private static final long MILLIS_TICK_INTERVAL = 100;
    private GridView gridView;
    private TextView resultsView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ram;
    }

    @Override
    public GameResult getGameResultImpl() {
        return new RamResult(NUMBERS_COUNT);
    }

    @Override
    public RamResult getGameResult() {
        return (RamResult) super.getGameResult();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gridView = (GridView) findViewById(R.id.numbers_grid);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.item_ram_number, R.id.number_view, generateNumbers());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectNumber(view);
            }
        });

        resultsView = (TextView) findViewById(R.id.entered_numbers);

        startTimer(SECONDS_GAME * GameActivity.MILLIS_PER_SECOND, MILLIS_TICK_INTERVAL);
    }

    private Integer[] generateNumbers() {
        Set<Integer> set = new HashSet<>(NUMBERS_COUNT);
        Random rand = new Random();
        int i = 0;
        while (i < NUMBERS_COUNT) {
            Integer number = Utils.generateRandomInt(rand, MIN_NUMBER, MAX_NUMBER);
            if (!set.contains(number)) {
                set.add(number);
                i++;
            }
        }
        return set.<Integer>toArray(new Integer[NUMBERS_COUNT]);
    }

    private void flushSelection() {
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View view = gridView.getChildAt(i);
            if (view.getTag() != null) {
                view.setTag(null);
                view.setBackgroundResource(R.drawable.border);
            }
        }
    }

    private void selectNumber(View view) {
        Object tag = view.getTag();
        TextView textView = (TextView) view.findViewById(R.id.number_view);
        Integer number = Integer.valueOf(String.valueOf(textView.getText()));
        RamResult gameResult = getGameResult();

        if (tag != null) {
            flushSelection();

            String resultsStr = String.valueOf(resultsView.getText());
            if (resultsStr != null && !resultsStr.isEmpty()) {
                resultsView.setText(resultsStr + ", " + number);
            } else {
                resultsView.setText(String.valueOf(number));
            }
            gameResult.addResult(number);
            if (gameResult.isGameOver()) {
                stopGame();
            }
        } else {
            if (!gameResult.containResult(number)) {
                flushSelection();
                view.setTag(new Object());
                view.setBackgroundResource(R.drawable.border_red);
            }
        }
    }

}
