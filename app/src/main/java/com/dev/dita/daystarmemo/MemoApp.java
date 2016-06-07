package com.dev.dita.daystarmemo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.baasbox.android.BaasBox;
import com.dev.dita.daystarmemo.model.database.Data;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MemoApp extends MultiDexApplication {
    private BaasBox baasBox;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize baasbox
        baasBox = BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                //.setApiDomain("dita.dev.ngrok.io")
                //.setPort(80)
                .setApiDomain(BuildConfig.HOST)
                .setPort(BuildConfig.PORT)
                .setAppCode(BuildConfig.APP_KEY)
                .setSessionTokenExpires(false)
                .setPushSenderId(BuildConfig.SENDERID)
                .init();

        // Initialize database
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        //Realm.deleteRealm(realmConfiguration);
        Realm.setDefaultConfiguration(realmConfiguration);

        Data data = new Data(getApplicationContext());
        data.fillData();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
