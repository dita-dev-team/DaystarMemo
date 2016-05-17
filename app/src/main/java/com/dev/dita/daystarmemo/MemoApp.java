package com.dev.dita.daystarmemo;

import android.app.Application;

import com.baasbox.android.BaasBox;

public class MemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("dita.dev.ngrok.io")
                .setAppCode("1234567890")
                .init();
    }
}
