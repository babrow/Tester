package com.babrow.tester.model;

import com.babrow.tester.App;
import com.babrow.tester.R;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by babrow on 28.02.2016.
 */
public class RamResult implements GameResult<Integer> {
    private static final long TEST_ID = 3;

    private Set<Integer> data;
    private Map<String, String> overallResult;

    private final int resultCount;

    public RamResult(int resultCount) {
        this.resultCount = resultCount;
        this.data = new LinkedHashSet<>();
    }

    @Override
    public Map<String, String> toParams() {
        Map<String, String> params = new HashMap<>();
        params.put(TEST_ID_FIELD, String.valueOf(getTestId()));
        Account account = App.getAccount();
        if (account != null) {
            params.put(ACCOUNT_ID_FIELD, String.valueOf(account.getId()));
        }
        params.putAll(getOverallResult());
        return params;
    }

    @Override
    public String toMessage() {
        Map<String, String> overallResult = getOverallResult();
        return String.format(App.getContext().getResources().getString(R.string.test3_results_description),
                data.size(), overallResult.get(RESULT_FIELD), overallResult.get(RESULT_DESCR_FIELD));
    }

    @Override
    public long getTestId() {
        return TEST_ID;
    }

    @Override
    public void flush() {
        data.clear();
    }

    @Override
    public void addResult(Integer result) {
        data.add(result);
    }

    public boolean containResult(Integer number) {
        return data.contains(number);
    }

    public boolean isGameOver() {
        return data.size() == resultCount;
    }

    @Override
    public Map<String, String> getOverallResult() {
        if (overallResult != null) {
            return overallResult;
        }
        overallResult = new HashMap<>();

        int errorsCnt = 0;
        Integer prev = null;
        for (Integer number: data) {
            if (prev != null && number < prev) {
                errorsCnt++;
            }
            prev = number;
        }

        int res = data.size() - errorsCnt;
        overallResult.put(App.getContext().getResources().getString(R.string.test3_results_save_numbers),
                String.valueOf(data.size()));
        overallResult.put(App.getContext().getResources().getString(R.string.test3_results_save_errors),
                String.valueOf(errorsCnt));

        String resStr;
        if (res >= 23) {
            resStr = App.getContext().getResources().getString(R.string.test3_results_vh);
        } else if (res >= 21 && res < 23) {
            resStr = App.getContext().getResources().getString(R.string.test3_results_h);
        } else if (res >= 14 && res < 21) {
            resStr = App.getContext().getResources().getString(R.string.test3_results_n);
        } else if (res >= 9 && res < 14) {
            resStr = App.getContext().getResources().getString(R.string.test3_results_l);
        } else {
            resStr = App.getContext().getResources().getString(R.string.test3_results_vl);
        }
        overallResult.put(RESULT_FIELD, String.valueOf(res));
        overallResult.put(RESULT_DESCR_FIELD, resStr);

        return overallResult;
    }
}
