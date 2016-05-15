package com.dev.dita.daystarmemo.ui.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dev.dita.daystarmemo.R;

public class WelcomeActivity extends AppCompatActivity {

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //EventBus.getDefault().register(this);
        //ButterKnife.bind(this);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (fragment == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment, new WelcomeFragment()).commit();
        }
    }

    public void showRegisterView(View view) {
        Fragment register = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, register).addToBackStack(null).commit();
    }

    public void showLoginView(View view) {
        Fragment login = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, login).addToBackStack(null).commit();
    }

    public void login(View view) {
        loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        loginFragment.login(view);
    }
}
