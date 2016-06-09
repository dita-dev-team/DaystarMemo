package com.dev.dita.daystarmemo.services;

import android.os.Bundle;

import com.dev.dita.daystarmemo.receivers.NewMessageNotification;
import com.google.android.gms.gcm.GcmListenerService;

public class GcmService extends GcmListenerService {
    public GcmService() {
    }

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        String message = bundle.getString("message", "no message");
        NewMessageNotification.notify(this, message, 1);
    }
}
