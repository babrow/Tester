package com.babrow.tester.model;

import com.babrow.tester.App;
import com.babrow.tester.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrafficResult implements GameResult<TrafficResult.TrafficResultRow> {
    private static final long TEST_ID = 2;
    private Map<Boolean, List<TrafficResultRow>> data;
    private Map<String, String> overallResult;

    public TrafficResult() {
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
        if (results == null) {
            return App.getContext().getString(R.string.test_results_wrong);
        }
        String errorsCnt = results.get(App.getContext().getString(R.string.test2_results_save_errors));
        String react = results.get(App.getContext().getString(R.string.test2_results_save_reaction));
        String overall = results.get(RESULT_DESCR_FIELD);
        if (TEST_RESULTS_WRONG.equals(overall)) {
            return TEST_RESULTS_WRONG;
        } else {
            return String.format(App.getContext().getString(R.string.test2_results_description), errorsCnt, react, overall);
        }
    }

    @Override
    public long getTestId() {
        return TEST_ID;
    }

    @Override
    public void addResult(TrafficResultRow result) {
        List<TrafficResultRow> res = data.get(result.isError());
        if (res == null) {
            res = new ArrayList<>();
            data.put(result.isError(), res);
        }
        res.add(result);
    }

    @Override
    public Map<String, String> getOverallResult() {
        if (overallResult != null) {
            return overallResult;
        }

        overallResult = new HashMap<>();
        int posCnt = data.get(false) != null ? data.get(false).size() : 0;
        if (posCnt == 0) {
            overallResult.put(RESULT_DESCR_FIELD, TEST_RESULTS_WRONG);
        } else {
            int errorsCnt = data.get(true) != null ? data.get(true).size() : 0;

            int react = 0;
            for (TrafficResultRow row : data.get(false)) {
                react += row.getReaction();
            }
            react = react / posCnt;
            errorsCnt = errorsCnt * 100 / (errorsCnt + posCnt);

            overallResult.put(App.getContext().getString(R.string.test2_results_save_errors), String.valueOf(errorsCnt));
            overallResult.put(App.getContext().getString(R.string.test2_results_save_reaction), String.valueOf(react));

            String resStr = TEST_RESULTS_WRONG;
            if (react <= 429) {
                resStr = App.getContext().getResources().getString(R.string.test2_results_vh);
            } else if (react > 429 && react <= 562) {
                resStr = App.getContext().getResources().getString(R.string.test2_results_h);
            } else if (react > 562 && react <= 714) {
                resStr = App.getContext().getResources().getString(R.string.test2_results_n);
            } else if (react > 714 && react <= 1000) {
                resStr = App.getContext().getResources().getString(R.string.test2_results_l);
            } else if (react > 1000) {
                resStr = App.getContext().getResources().getString(R.string.test2_results_vl);
            }
            overallResult.put(RESULT_FIELD, String.valueOf(react));
            overallResult.put(RESULT_DESCR_FIELD, resStr);
        }
        return overallResult;
    }

    public static class TrafficResultRow {
        private boolean isError;
        private long reaction;

        public TrafficResultRow(boolean isError, long reaction) {
            this.isError = isError;
            this.reaction = reaction;
        }

        public boolean isError() {
            return isError;
        }

        public long getReaction() {
            return reaction;
        }
    }
}
