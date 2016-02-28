package com.babrow.tester.model;

import com.babrow.tester.App;
import com.babrow.tester.R;

import java.util.Map;

public interface GameResult<T> {
    String TEST_RESULTS_WRONG = App.getContext().getString(R.string.test_results_wrong);
    String TEST_ID_FIELD = "testId";
    String ACCOUNT_ID_FIELD = "accountId";
    String RESULT_FIELD = "result";
    String RESULT_DESCR_FIELD = "resultDescr";

    void flush();

    Map<String, String> toParams();

    String toMessage();

    long getTestId();

    void addResult(T result);

    Map<String, String> getOverallResult();
}
