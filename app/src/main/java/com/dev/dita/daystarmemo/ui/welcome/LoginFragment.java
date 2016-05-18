package com.dev.dita.daystarmemo.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dev.dita.daystarmemo.PrefSettings;
import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.controller.utils.UIUtils;
import com.dev.dita.daystarmemo.ui.main.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {
    @BindView(R.id.login_username)
    TextInputEditText usernameTxt;
    @BindView(R.id.login_pass)
    TextInputEditText passwordTxt;

    String username;
    String password;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @OnClick(R.id.login_submit_button)
    public void login() {
        UIUtils.hideKeyboard(getActivity());
        // Verify all input has been provided
        if (TextUtils.isEmpty(usernameTxt.getText().toString()) || TextUtils.isEmpty(passwordTxt.getText().toString())) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        username = usernameTxt.getText().toString().trim();
        password = passwordTxt.getText().toString().trim();
        // Post a login event
        EventBus.getDefault().post(new UserBus.LoginEvent(username, password));
    }

    @Subscribe
    public void onEvent(UserBus.LoginResult loginResult) {
        EventBus.getDefault().post(new UserBus.Notify());
        if (loginResult.error) {
            Toast.makeText(getContext(), loginResult.message, Toast.LENGTH_LONG).show();
        } else {
            PrefSettings.setLoggedIn(getContext(), true);
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }
}
