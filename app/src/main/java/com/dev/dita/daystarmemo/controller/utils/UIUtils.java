package com.dev.dita.daystarmemo.controller.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.inputmethod.InputMethodManager;

public class UIUtils {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    public static void setAnimation(SwipeRefreshLayout swipeRefreshLayout, boolean value) {
        swipeRefreshLayout.setEnabled(value);
        swipeRefreshLayout.setRefreshing(value);
    }
}
