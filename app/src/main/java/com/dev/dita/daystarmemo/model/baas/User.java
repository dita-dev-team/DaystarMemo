package com.dev.dita.daystarmemo.model.baas;

import android.util.Log;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.dev.dita.daystarmemo.controller.bus.UserBus;

import org.greenrobot.eventbus.EventBus;

public class User {
    public static final String TAG = User.class.getName();

    public static void loginUser(String username, String password) {
        final BaasUser user = BaasUser.withUserName(username)
                .setPassword(password);

        user.login(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> baasResult) {
                if (baasResult.isSuccess()) {

                } else {
                    UserBus.LoginResult loginResult = new UserBus.LoginResult();
                    loginResult.error = true;
                    loginResult.message = "Invalid username or password";
                    EventBus.getDefault().post(loginResult);
                }
            }
        });
    }

    public static void createUser(String username, String email, String password) {
        final BaasUser user = BaasUser.withUserName(username)
                .setPassword(password);

        JsonObject extras = user.getScope(BaasUser.Scope.FRIEND)
                .put("email", email);

        user.signup(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> baasResult) {
                if (baasResult.isSuccess()) {

                } else {
                    UserBus.RegisterResult registerResult = new UserBus.RegisterResult();
                    registerResult.error = true;
                    registerResult.message = "Username already exists";
                    EventBus.getDefault().post(registerResult);
                    Log.e(TAG, baasResult.error().getMessage());
                }
            }
        });
    }
}
