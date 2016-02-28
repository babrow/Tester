package com.babrow.tester;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.babrow.tester.model.Account;
import com.babrow.tester.utils.http.GenericRequest;
import com.google.gson.Gson;

/**
 * Created by babrow on 06.02.2016.
 */
public class App extends Application {
    private final class SETTINGS {
        private static final String SETTINGS_FILE = "SETTINGS";
        private static final String SETTINGS_ACCOUNT_KEY = "ACCOUNT";
    }

    public static final class SERVER_API {
        public static final String URL = "http://93.95.101.24:9000/Api/";
        public static final String LOGIN_URL = URL + "login";
        public static final String SAVE_RESULT_URL = URL + "saveResult";
    }

    private static Context mContext;

    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mRequestQueue = Volley.newRequestQueue(this);
    }

    public static Context getContext() {
        return mContext;
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public static Account getAccount() {
        SharedPreferences prefs = getContext().getSharedPreferences(SETTINGS.SETTINGS_FILE, MODE_PRIVATE);
        String accountStr = prefs.getString(SETTINGS.SETTINGS_ACCOUNT_KEY, null);
        if (accountStr != null) {
            return new Gson().fromJson(accountStr, Account.class);
        }
        return null;
    }

    public static void setAccount(Account account) {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(SETTINGS.SETTINGS_FILE, MODE_PRIVATE).edit();
        editor.putString(SETTINGS.SETTINGS_ACCOUNT_KEY, new Gson().toJson(account));
        editor.commit();
    }

    public static void flushAccount() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(SETTINGS.SETTINGS_FILE, MODE_PRIVATE).edit();
        editor.remove(SETTINGS.SETTINGS_ACCOUNT_KEY);
        editor.commit();
    }

    public static <T> void sendRequest(GenericRequest<T> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }
}
