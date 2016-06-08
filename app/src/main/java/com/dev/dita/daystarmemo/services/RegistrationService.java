package com.dev.dita.daystarmemo.services;

import android.app.IntentService;
import android.content.Intent;

import com.dev.dita.daystarmemo.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegistrationService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID myID = InstanceID.getInstance(this);
        String registrationToken = myID.getToken(
                getString(R.string.),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                null
        );
    }
}
