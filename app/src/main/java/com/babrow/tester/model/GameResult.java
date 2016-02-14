package com.babrow.tester.model;

import java.util.Map;

/**
 * Created by babrow on 06.02.2016.
 */
public interface GameResult<T> {
    String TEST_ID_FIELD = "testId";
    String ACCOUNT_ID_FIELD = "accountId";

    void flush();

    Map<String, String> toParams();

    String toMessage();

    long getTestId();

    long getAccountId();

    void addResult(T result);
}
