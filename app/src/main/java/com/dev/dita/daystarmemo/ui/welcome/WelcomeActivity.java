package com.dev.dita.daystarmemo.ui.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.model.baas.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WelcomeActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;

    public void init() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_animation);
        swipeRefreshLayout.setColorSchemeResources(R.color.baseColor1, R.color.baseColor2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        EventBus.getDefault().register(this);
        init();
        setAnimation(false);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fragment == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, new WelcomeFragment()).commit();
        }
    }

    public void showRegisterView(View view) {
        Fragment register = new RegisterFragment();
        String name = register.getClass().getName();
        if (!getSupportFragmentManager().popBackStackImmediate(name, 0)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, register).addToBackStack(name).commit();
        }
    }

    public void showLoginView(View view) {
        Fragment login = new LoginFragment();
        String name = login.getClass().getName();
        if (!getSupportFragmentManager().popBackStackImmediate(name, 0)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, login).addToBackStack(name).commit();
        }
    }

    @Subscribe
    public void onEvent(UserBus.LoginEvent loginEvent) {
        setAnimation(true);
        User.loginUser(loginEvent.username, loginEvent.password);
    }

    @Subscribe
    public void onEvent(UserBus.RegisterEvent registerEvent) {
        setAnimation(true);
        User.createUser(registerEvent.username, registerEvent.email, registerEvent.password);
    }

    @Subscribe
    public void onEvent(UserBus.Notify notify) {
        setAnimation(false);
    }

    public void setAnimation(Boolean value) {
        swipeRefreshLayout.setEnabled(value);
        swipeRefreshLayout.setRefreshing(value);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
