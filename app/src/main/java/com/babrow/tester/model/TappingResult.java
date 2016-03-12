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

        boolean bResN = true;
        boolean bResVH = true;
        boolean bResH = true;
        boolean bResVL = true;
        int x1 = 0;
        if (data.size() == 6 && data.get(0) != 0) {
            x1 = data.get(0);

            for (int i = 1; i < data.size(); i++) {
                int x = data.get(i);
                float div = ((float) (x - x1) / x1) * 100;

                if (Math.abs(div) > 5) {
                    bResN = false;
                }
                if (i < 4 && div < 0) {
                    bResH = false;
                }
                if (i < 5 && div < 0) {
                    bResVH = false;
                }
                if (div > 0) {
                    bResVL = false;
                }
            }
        }

        overallResult = new HashMap<>();
        if (x1 == 0) {
            overallResult.put(RESULT_FIELD, String.valueOf(-1));
            overallResult.put(RESULT_DESCR_FIELD, TEST_RESULTS_WRONG);
        } else {
            int res;
            String resStr;

            if (bResN) {
                res = 2;
                resStr = App.getContext().getResources().getString(R.string.test1_results_n);
            } else if (bResVH){
                res = 4;
                resStr = App.getContext().getResources().getString(R.string.test1_results_vh);
            } else if (bResH) {
                res = 3;
                resStr = App.getContext().getResources().getString(R.string.test1_results_h);
            } else if (bResVL) {
                res = 0;
                resStr = App.getContext().getResources().getString(R.string.test1_results_vl);
            } else {
                res = 1;
                resStr = App.getContext().getResources().getString(R.string.test1_results_l);
            }

            overallResult.put(RESULT_FIELD, String.valueOf(res));
            overallResult.put(RESULT_DESCR_FIELD, resStr);
        }
        return overallResult;
    }
}
