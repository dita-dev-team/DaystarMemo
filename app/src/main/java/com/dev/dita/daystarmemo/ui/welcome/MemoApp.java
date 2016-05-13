package com.dev.dita.daystarmemo.ui.welcome;

import android.app.Application;

import com.baasbox.android.BaasBox;

public class MemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("192.168.7.74")
                .setPort(9000)
                .setAppCode("1234567890")
                .init();
    }
}
