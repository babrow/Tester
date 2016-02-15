package com.babrow.tester.model;

import com.babrow.tester.App;
import com.babrow.tester.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TappingResult implements GameResult<Integer> {
    private static final long TEST_ID = 1;
    private List<Integer> data;

    public TappingResult() {
        this.data = new ArrayList<>();
    }

    public void addResult(int result) {
        data.add(result);
    }

    @Override
    public void flush() {
        data.clear();
    }

    @Override
    public Map<String, String> toParams() {
        Map<String, String> params = new HashMap<>();
        params.put(TEST_ID_FIELD, String.valueOf(getTestId()));
        Account account = App.getAccount();
        if (account != null) {
            params.put(ACCOUNT_ID_FIELD, String.valueOf(account.getId()));
        }
        for (int i = 0; i < data.size(); i++) {
            params.put(String.format(App.getContext().getResources().getString(R.string.test1_results_save), i + 1), String.valueOf(data.get(i)));
        }
        return params;
    }

    @Override
    public String toMessage() {
        String resStr = data.toString().replaceAll("\\[|\\]", "");
        return String.format(App.getContext().getResources().getString(R.string.test1_results_description), resStr);
    }

    @Override
    public long getTestId() {
        return TEST_ID;
    }

    @Override
    public void addResult(Integer result) {
        data.add(result);
    }
}
