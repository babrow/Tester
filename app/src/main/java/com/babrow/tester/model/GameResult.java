package com.babrow.tester.model;

import java.util.Map;

public interface GameResult<T> {
    String TEST_ID_FIELD = "testId";
    String ACCOUNT_ID_FIELD = "accountId";

    void flush();

    Map<String, String> toParams();

    String toMessage();

    long getTestId();

    void addResult(T result);
}
