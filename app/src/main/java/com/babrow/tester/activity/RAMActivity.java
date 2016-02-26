package com.babrow.tester.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.babrow.tester.R;
import com.babrow.tester.model.GameResult;

public class RAMActivity extends GameActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ram;
    }

    @Override
    public GameResult getGameResultImpl() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] data = new String[25];

        for (int i = 1; i <= 25; i++) {
            data[i - 1] = String.valueOf(i);
        }

        GridView gvMain;
        ArrayAdapter<String> adapter;

        adapter = new ArrayAdapter<>(this, R.layout.item_ram_number, R.id.number_view, data);
        gvMain = (GridView) findViewById(R.id.numbers_grid);
        gvMain.setAdapter(adapter);
    }

}
