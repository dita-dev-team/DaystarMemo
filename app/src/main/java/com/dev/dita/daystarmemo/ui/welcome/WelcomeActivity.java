package com.dev.dita.daystarmemo.ui.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.controller.utils.UIUtils;
import com.dev.dita.daystarmemo.model.baas.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The type Welcome activity.
 */
public class WelcomeActivity extends AppCompatActivity {

    /**
     * The Swipe refresh layout.
     */
    @BindView(R.id.welcome_refresh_animation)
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Init.
     */
    public void init() {
        swipeRefreshLayout.setColorSchemeResources(R.color.baseColor1, R.color.baseColor2);
        UIUtils.setAnimation(swipeRefreshLayout, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fragment == null) {
            // Set WelcomeFragment as the active fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, new WelcomeFragment()).commit();
        }
    }

    /**
     * Show register view.
     *
     * @param view the view
     */
// Sets RegisterFragment as the active fragment
    public void showRegisterView(View view) {
        Fragment register = new RegisterFragment();
        String name = register.getClass().getName();
        if (!getSupportFragmentManager().popBackStackImmediate(name, 0)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, register).addToBackStack(name).commit();
        }
    }

    /**
     * Show login view.
     *
     * @param view the view
     */
// Sets LoginFragment as the active fragment
    public void showLoginView(View view) {
        Fragment login = new LoginFragment();
        String name = login.getClass().getName();
        if (!getSupportFragmentManager().popBackStackImmediate(name, 0)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, login).addToBackStack(name).commit();
        }
    }

    /**
     * On event.
     *
     * @param loginEvent the login event
     */
    @Subscribe
    public void onEvent(UserBus.LoginEvent loginEvent) {
        UIUtils.setAnimation(swipeRefreshLayout, true);
        User.loginUser(loginEvent.username, loginEvent.password);
    }

    /**
     * On event.
     *
     * @param registerEvent the register event
     */
    @Subscribe
    public void onEvent(UserBus.RegisterEvent registerEvent) {
        UIUtils.setAnimation(swipeRefreshLayout, true);
        User.createUser(registerEvent.username, registerEvent.email, registerEvent.password);
    }

    /**
     * On event.
     *
     * @param notify the notify
     */
    @Subscribe
    public void onEvent(UserBus.Notify notify) {
        UIUtils.setAnimation(swipeRefreshLayout, false);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
