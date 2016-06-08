package com.dev.dita.daystarmemo.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MemoReceiver extends WakefulBroadcastReceiver {
    public MemoReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras().get("from".equals(SENDER)))
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String message = gcm.getMessageType(intent);
        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(message)) {
            Bundle extras = intent.getExtras();
            String messageText = extras.getString("message", "no message");
            NewMemoNotification.notify(context, messageText, 1);
        }
    }
}
