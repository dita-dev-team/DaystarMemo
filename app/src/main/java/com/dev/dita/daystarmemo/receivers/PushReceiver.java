package com.dev.dita.daystarmemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class PushReceiver extends BroadcastReceiver {
    public PushReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String message = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(message)) {
            Bundle extras = intent.getExtras();
            String messageText = extras.getString("message", "no message");
            NewMessageNotification.notify(context, messageText, 1);
        }
    }
}
