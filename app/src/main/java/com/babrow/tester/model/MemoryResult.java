package com.babrow.tester.model;

import com.babrow.tester.App;
import com.babrow.tester.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryResult implements GameResult<MemoryResult.MemoryResultRow> {
    private static final long TEST_ID = 4;
    private Map<Integer, List<Boolean>> data;
    private Map<String, String> overallResult;

    public MemoryResult() {
        data = new HashMap<>();
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

        params.putAll(getOverallResult());
        return params;
    }

    @Override
    public String toMessage() {
        Map<String, String> results = getOverallResult();

        String volume = results.get(App.getContext().getString(R.string.test4_results_save_volume));
        String overall = results.get(RESULT_DESCR_FIELD);
        return String.format(App.getContext().getString(R.string.test4_results_description), volume, overall);
    }

    @Override
    public long getTestId() {
        return TEST_ID;
    }

    @Override
    public void addResult(MemoryResultRow result) {
        List<Integer> viewedSums = new ArrayList<>();

        for (int i = 0; i < result.getViewedNumbers().size(); i++) {
            if (i % 2 != 0) {
                int prevNumber = result.getViewedNumbers().get(i - 1);
                int thisNumber = result.getViewedNumbers().get(i);
                viewedSums.add(thisNumber + prevNumber);
            }
        }
        int before = viewedSums.size();
        viewedSums.retainAll(result.getInputSums());
        int after = viewedSums.size();
        boolean res = before == after;

        int iteration = result.getIterationCnt();
        List<Boolean> results = data.get(iteration);
        if (results == null) {
            results = new ArrayList<>();
            data.put(iteration, results);
        }
        results.add(res);
    }

    @Override
    public Map<String, String> getOverallResult() {
        if (overallResult != null) {
            return overallResult;
        }

        int a = -2;
        for (List<Boolean> series : data.values()) {
            boolean errorFound = false;
            for (int i = 0; i < series.size(); i++) {
                boolean res = series.get(i);
                if (!res) {
                    errorFound = true;
                    if (a > i - 1 || a == -2) {
                        a = i - 1;
                    }
                    break;
                }
            }
            if (!errorFound && a != -2) {
                a = (series.size() - 1);
            }
        }
        a = a == -1 ? 0 : a;

        int m = 0;
        for (List<Boolean> series : data.values()) {
            for (int i = 0; i < series.size(); i++) {
                boolean res = series.get(i);
                if (res && i > a) {
                    m++;
                }
            }
        }
        a = a + 2;
        double v = a + m / 4 + 0.5;

        overallResult = new HashMap<>();
        overallResult.put(App.getContext().getString(R.string.test4_results_save_volume), String.valueOf(v));

        String resStr;
        if (v >= 4.2) {
            resStr = App.getContext().getResources().getString(R.string.test4_results_h);
        } else {
            resStr = App.getContext().getResources().getString(R.string.test4_results_l);
        }
        ;
        overallResult.put(RESULT_FIELD, String.valueOf(v));
        overallResult.put(RESULT_DESCR_FIELD, resStr);

        return overallResult;
    }

    public static class MemoryResultRow {
        final private List<Integer> viewedNumbers;
        final private List<Integer> inputSums;
        final private int iterationCnt;

        public MemoryResultRow(List<Integer> viewedNumbers, List<Integer> inputSums,
                               int iterationCnt) {
            this.viewedNumbers = viewedNumbers;
            this.inputSums = inputSums;
            this.iterationCnt = iterationCnt;
        }

        public List<Integer> getViewedNumbers() {
            return viewedNumbers;
        }

        public List<Integer> getInputSums() {
            return inputSums;
        }

        public int getIterationCnt() {
            return iterationCnt;
        }
    }
}
