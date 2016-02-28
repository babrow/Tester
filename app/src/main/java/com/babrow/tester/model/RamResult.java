package com.babrow.tester.model;

import com.babrow.tester.App;
import com.babrow.tester.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by babrow on 28.02.2016.
 */
public class RamResult implements GameResult<RamResult.RamResultRaw> {
    private static final long TEST_ID = 3;
    private static final String RESULTS_TIME_STR = "RESULTS_TIME_STR";
    private static final String RESULTS_ERRORS_STR = "RESULTS_ERRORS_STR";

    private List<RamResultRaw> data;
    private Map<String, String> overallResult;

    private final int resultCount;
    private final int resultInterval;

    public RamResult(int resultCount, int resultInterval) {
        this.resultCount = resultCount;
        this.resultInterval = resultInterval;
        this.data = new ArrayList<>();
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
                overallResult.get(RESULTS_TIME_STR), overallResult.get(RESULTS_ERRORS_STR), overallResult.get(RESULT_DESCR_FIELD));
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
    public void addResult(RamResultRaw result) {
        data.add(result);
    }

    public boolean containResult(Integer number) {
        for (RamResultRaw result : data) {
            if (number.equals(result.getNumber())) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return data.size() == resultCount;
    }

    @Override
    public Map<String, String> getOverallResult() {
        if (overallResult != null) {
            return overallResult;
        }
        if (data.size() < resultCount) {
            overallResult.put(RESULT_DESCR_FIELD, TEST_RESULTS_WRONG);
        } else {
            overallResult = new HashMap<>();

            ArrayList<Integer> errors = new ArrayList<>();
            ArrayList<Integer> times = new ArrayList<>();
            Date firstDate = new Date();
            int errorsCnt = 0;
            int res = 0;
            for (int i = 0; i < data.size(); i++) {
                RamResultRaw raw = data.get(i);
                if (i == 0) {
                    firstDate = raw.getLogDate();
                } else {
                    RamResultRaw prevRaw = data.get(i - 1);
                    if (raw.getNumber() < prevRaw.getNumber()) {
                        errorsCnt++;
                    }
                }

                if ((i + 1) % resultInterval == 0) {
                    errors.add(errorsCnt);
                    times.add((int) (raw.getLogDate().getTime() - firstDate.getTime()) / 1000);
                    firstDate = raw.getLogDate();
                    res += errorsCnt;
                    errorsCnt = 0;
                }
            }

            for (int i = 0; i < times.size(); i++) {
                overallResult.put(String.format(App.getContext().getResources().getString(R.string.test3_results_save_time), i + 1),
                        String.valueOf(times.get(i)));
            }
            overallResult.put(RESULTS_TIME_STR, times.toString().replaceAll("\\[|\\]", ""));

            for (int i = 0; i < errors.size(); i++) {
                overallResult.put(String.format(App.getContext().getResources().getString(R.string.test3_results_save_errors), i + 1),
                        String.valueOf(errors.get(i)));
            }
            overallResult.put(RESULTS_ERRORS_STR, errors.toString().replaceAll("\\[|\\]", ""));

            res = resultCount - res;
            String resStr;
            if (res >= 22) {
                resStr = App.getContext().getResources().getString(R.string.test3_results_g);
            } else {
                resStr = App.getContext().getResources().getString(R.string.test3_results_b);
            }
            overallResult.put(RESULT_FIELD, String.valueOf(res));
            overallResult.put(RESULT_DESCR_FIELD, resStr);
        }
        return overallResult;
    }

    public static class RamResultRaw {
        private Date logDate;
        private Integer number;


        public RamResultRaw(Date logDate, Integer number) {
            this.logDate = logDate;
            this.number = number;
        }

        public Date getLogDate() {
            return logDate;
        }

        public Integer getNumber() {
            return number;
        }
    }
}
