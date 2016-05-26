package com.dev.dita.daystarmemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.baasbox.android.BaasBox;

public class MemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                //.setApiDomain("dita.dev.ngrok.io")
                //.setPort(80)
                .setSessionTokenExpires(false)
                .setApiDomain("192.168.43.206")
                .setPort(9000)
                .setAppCode("1234567890")
                .init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
