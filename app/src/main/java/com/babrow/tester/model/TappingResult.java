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
    private Map<String, String> overallResult;

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
        params.putAll(getOverallResult());
        return params;
    }

    @Override
    public String toMessage() {
        String resStr = data.toString().replaceAll("\\[|\\]", "");
        Map<String, String> overallResult = getOverallResult();
        return String.format(App.getContext().getResources().getString(R.string.test1_results_description),
                resStr, overallResult.get(RESULT_FIELD), overallResult.get(RESULT_DESCR_FIELD));
    }

    @Override
    public long getTestId() {
        return TEST_ID;
    }

    @Override
    public void addResult(Integer result) {
        data.add(result);
    }

    @Override
    public Map<String, String> getOverallResult() {
        if (overallResult != null) {
            return overallResult;
        }

        float res = 0;
        int x1 = 0;
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                x1 = data.get(i);
            } else {
                int x = data.get(i);
                res = res + x - x1;
            }
        }

        overallResult = new HashMap<>();
        if (x1 == 0) {
            overallResult.put(RESULT_FIELD, String.format("%.2f", res));
            overallResult.put(RESULT_DESCR_FIELD, TEST_RESULTS_WRONG);
        } else {
            res = res / x1;

            String resStr = TEST_RESULTS_WRONG;
            if (res >= 1.5) {
                resStr = App.getContext().getResources().getString(R.string.test1_results_vh);
            } else if (res >= 1.2 && res < 1.5) {
                resStr = App.getContext().getResources().getString(R.string.test1_results_h);
            } else if (res >= 0.8 && res < 1.2) {
                resStr = App.getContext().getResources().getString(R.string.test1_results_n);
            } else if (res >= 0.5 && res < 0.8) {
                resStr = App.getContext().getResources().getString(R.string.test1_results_l);
            } else if (res < 0.5) {
                resStr = App.getContext().getResources().getString(R.string.test1_results_vl);
            }

            overallResult.put(RESULT_FIELD, String.format("%.2f", res));
            overallResult.put(RESULT_DESCR_FIELD, resStr);
        }
        return overallResult;
    }
}
