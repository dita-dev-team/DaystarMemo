package com.dev.dita.daystarmemo.model.database;

import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.dev.dita.daystarmemo.controller.bus.UserBus;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {
    @PrimaryKey
    public String username;
    public String name;
    public String image;
    public Boolean isGroup;
    public RealmList<Memo> memos;

    public static void addUser(final BaasUser user) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                User newUser = new User();
                newUser.username = user.getName();
                JsonObject extraData = user.getScope(BaasUser.Scope.FRIEND);
                if (extraData != null) {
                    newUser.name = extraData.getString("name", "");
                    newUser.image = extraData.getString("image", "");
                }
                realm.copyToRealm(newUser);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                UserBus.AddUserResult userResult = new UserBus.AddUserResult();
                userResult.error = false;
                EventBus.getDefault().post(userResult);

                if (!realm.isInTransaction()) {
                    realm.close();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                UserBus.AddUserResult userResult = new UserBus.AddUserResult();
                userResult.error = true;
                EventBus.getDefault().post(userResult);

                if (!realm.isInTransaction()) {
                    realm.close();
                }
            }
        });
    }
}
