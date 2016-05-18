package com.dev.dita.daystarmemo.model.baas;

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.dev.dita.daystarmemo.controller.bus.UserBus;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * A java class which handles all user-management related actions to and from the baas
 */
public class User {
    public static final String TAG = User.class.getName();


    /**
     * logs in a user
     *
     * @param username user's username
     * @param password user's password
     */
    public static void loginUser(String username, String password) {
        final BaasUser user = BaasUser.withUserName(username)
                .setPassword(password);

        user.login(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> baasResult) {
                UserBus.LoginResult loginResult = new UserBus.LoginResult();
                if (baasResult.isSuccess()) {
                    loginResult.error = false;
                } else {
                    loginResult.error = true;
                    try {
                        throw baasResult.error();
                    } catch (BaasException e) {
                        if (e.getCause() instanceof UnknownHostException || e.getCause() instanceof SocketTimeoutException) {
                            loginResult.message = "Unable to connect";
                        } else {
                            loginResult.message = "Invalid username or password";
                        }
                    }
                    baasResult.error().printStackTrace();
                }
                EventBus.getDefault().post(loginResult);
            }
        });
    }


    /**
     * logout a user
     */
    public static void logoutUser() {
        BaasUser.current().logout(new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> baasResult) {
                UserBus.LogoutResult logoutResult = new UserBus.LogoutResult();
                if (baasResult.isSuccess()) {
                    logoutResult.error = false;
                } else {
                    logoutResult.error = true;
                    try {
                        throw baasResult.error();
                    } catch (BaasException e) {
                        if (e.getCause() instanceof UnknownHostException || e.getCause() instanceof SocketTimeoutException) {
                            logoutResult.message = "Unable to connect";
                        }
                    }
                }
                EventBus.getDefault().post(logoutResult);
            }
        });
    }


    /**
     * create a user
     *
     * @param username user's username
     * @param email    user's email
     * @param password user's password
     */
    public static void createUser(String username, String email, String password) {
        final BaasUser user = BaasUser.withUserName(username)
                .setPassword(password);

        JsonObject extras = user.getScope(BaasUser.Scope.FRIEND)
                .put("email", email);

        user.signup(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> baasResult) {
                UserBus.RegisterResult registerResult = new UserBus.RegisterResult();
                if (baasResult.isSuccess()) {

                } else {
                    registerResult.error = true;
                    try {
                        throw baasResult.error();
                    } catch (BaasException e) {
                        if (e.getCause() instanceof UnknownHostException || e.getCause() instanceof SocketTimeoutException) {
                            registerResult.message = "Unable to connect";
                        } else {
                            registerResult.message = "Username already exists";
                        }
                    }
                }
                EventBus.getDefault().post(registerResult);
            }
        });
    }
}
