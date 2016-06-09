package com.dev.dita.daystarmemo.model.baas;


import android.util.Log;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasCloudMessagingService;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.dev.dita.daystarmemo.controller.bus.MemoBus;
import com.dev.dita.daystarmemo.model.database.Memo;
import com.dev.dita.daystarmemo.model.objects.Recipient;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MemoBaas {
    /**
     * The constant TAG.
     */
    public static final String TAG = MemoBaas.class.getName();

    public static void sendMemo(final Memo memo, final List<Recipient> recipientList) {
        BaasDocument document = new BaasDocument("memos");
        document.put("body", memo.body);

        document.save(new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> baasResult) {
                MemoBus.SendMemoResult memoResult;
                if (baasResult.isSuccess()) {
                    Log.i(TAG, "Saved document successfully");
                    //Realm realm = Realm.getDefaultInstance();
                    boolean accessError = false;
                    for (Recipient recipient : recipientList) {
                        grantAccess(baasResult.value(), recipient.username);
                    }
                } else {
                    Log.i(TAG, "Failed to save document");
                    Log.i(TAG, baasResult.error().getMessage());
                    memoResult = new MemoBus.SendMemoResult();
                    memoResult.error = true;
                    EventBus.getDefault().post(memoResult);
                }
            }
        });
    }

    public static void grantAccess(BaasDocument document, String username) {
        document.grant(Grant.READ, username, new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> baasResult) {
                if (baasResult.isSuccess()) {
                    sendNotification();
                } else {
                    Log.i(TAG, "Failed to grant access");
                    MemoBus.SendMemoResult memoResult = new MemoBus.SendMemoResult();
                    memoResult.error = true;
                    EventBus.getDefault().post(memoResult);
                }
            }
        });
    }

    public static void sendNotification() {

        BaasBox.messagingService()
                .newMessage()
                .profiles(BaasCloudMessagingService.DEFAULT_PROFILE)
                .text("New Memo")
                .to(BaasUser.current())
                .send(new BaasHandler<Void>() {
                    @Override
                    public void handle(BaasResult<Void> baasResult) {
                        MemoBus.SendMemoResult memoResult = new MemoBus.SendMemoResult();
                        if (baasResult.isSuccess()) {
                            memoResult.error = false;
                        } else {
                            Log.i(TAG, "Failed to send notification");
                            memoResult.error = true;
                        }
                        EventBus.getDefault().post(memoResult);
                    }
                });
    }

    public static void revertSentMemo(BaasDocument document) {
        document.delete(new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> baasResult) {
                if (baasResult.isSuccess()) {
                    Log.i(TAG, "Successfully reverted document");
                }
            }
        });
    }
}
