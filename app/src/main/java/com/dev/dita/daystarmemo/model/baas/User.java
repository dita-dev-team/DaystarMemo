package com.dev.dita.daystarmemo.model.baas;

import android.util.Log;

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonException;
import com.baasbox.android.json.JsonObject;
import com.dev.dita.daystarmemo.controller.bus.UserBus;

import org.greenrobot.eventbus.EventBus;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;


/**
 * A java class which handles all user-management related actions to and from the baas
 */
public class User {
    /**
     * The constant TAG.
     */
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
                        if (e.getCause() instanceof UnknownHostException || e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof JsonException) {
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

    public static Boolean refreshUser() {
        final Boolean[] error = {false};
        BaasUser.current().refresh(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> baasResult) {
                if (baasResult.isSuccess()) {
                } else {
                    error[0] = true;
                }

            }
        });

        return error[0];
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
                        baasResult.error().printStackTrace();
                        throw baasResult.error();
                    } catch (BaasException e) {
                        if (e.getCause() instanceof UnknownHostException || e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof JsonException) {
                            logoutResult.message = "Unable to connect";
                        } else if (e.getCause() instanceof NullPointerException) {
                            logoutResult.message = "Session expired";
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
                    registerResult.error = false;
                } else {
                    registerResult.error = true;
                    try {
                        throw baasResult.error();
                    } catch (BaasException e) {
                        if (e.getCause() instanceof UnknownHostException || e.getCause() instanceof SocketTimeoutException || e.getCause() instanceof JsonException) {
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

    /**
     * Update user profile.
     *
     * @param details user profile details
     */
    public static void updateUserProfile(Map<String, String> details) {
        BaasUser user = BaasUser.current();
        for (Map.Entry<String, String> entry : details.entrySet()) {
            user.getScope(BaasUser.Scope.FRIEND).put(entry.getKey(), entry.getValue());
        }

        user.save(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> baasResult) {
                UserBus.ProfileUpdatedRemoteResult remoteResult = new UserBus.ProfileUpdatedRemoteResult();
                if (baasResult.isSuccess()) {
                    remoteResult.error = false;
                } else {
                    remoteResult.error = true;
                    Log.e(TAG, remoteResult.message);
                }
                EventBus.getDefault().post(remoteResult);

            }
        });
    }

    /**
     * Change user password
     *
     * @param newPassword new password to be set
     */
    public static void changePassword(String newPassword) {
        BaasUser user = BaasUser.current();
        user.changePassword(newPassword, new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> baasResult) {
                UserBus.PasswordChangeResult passwordResult = new UserBus.PasswordChangeResult();
                passwordResult.error = !baasResult.isSuccess();
                EventBus.getDefault().post(passwordResult);

            }
        });
    }

}
