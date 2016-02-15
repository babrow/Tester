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

    public TrafficResult() {
        data = new HashMap<>();
    }

    @Override
    public void flush() {
        data.clear();
    }

    private Map<String, String> getResults() {
        int posCnt = data.get(false) != null ? data.get(false).size() : 0;
        if (posCnt == 0) {
            return null;
        }
        int errorsCnt = data.get(true) != null ? data.get(true).size() : 0;

        int react = 0;
        for (TrafficResultRow row : data.get(false)) {
            react += row.getReaction();
        }
        react = react / posCnt;
        errorsCnt = errorsCnt * 100 / (errorsCnt + posCnt);

        Map<String, String> results = new HashMap<>();
        results.put(App.getContext().getString(R.string.test2_results_save_errors), String.valueOf(errorsCnt));
        results.put(App.getContext().getString(R.string.test2_results_save_reaction), String.valueOf(react));
        return results;
    }

    @Override
    public Map<String, String> toParams() {
        Map<String, String> params = new HashMap<>();
        params.put(TEST_ID_FIELD, String.valueOf(getTestId()));
        Account account = App.getAccount();
        if (account != null) {
            params.put(ACCOUNT_ID_FIELD, String.valueOf(account.getId()));
        }
        Map<String, String> results = getResults();
        if (results != null) {
            params.putAll(results);
        }
        return params;
    }

    @Override
    public String toMessage() {
        Map<String, String> results = getResults();
        if (results == null) {
            return App.getContext().getString(R.string.test_results_wrong);
        }
        String errorsCnt = results.get(App.getContext().getString(R.string.test2_results_save_errors));
        String react = results.get(App.getContext().getString(R.string.test2_results_save_reaction));
        return String.format(App.getContext().getString(R.string.test2_results_description), errorsCnt, react);
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
