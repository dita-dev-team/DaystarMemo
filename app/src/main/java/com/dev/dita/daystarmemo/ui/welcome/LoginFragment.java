package com.dev.dita.daystarmemo.ui.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.controller.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

public class LoginFragment extends Fragment {
    EditText usernameTxt;
    EditText passwordTxt;

    Button submitBtn;


    String username;
    String password;

    public LoginFragment() {
        // Required empty public constructor
    }

    public void init(View view) {
        usernameTxt = (EditText) view.findViewById(R.id.login_username);
        passwordTxt = (EditText) view.findViewById(R.id.login_pass);
        submitBtn = (Button) view.findViewById(R.id.login_submit_button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        init(view);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hideKeyboard(getActivity());
                if (TextUtils.isEmpty(usernameTxt.getText().toString()) || TextUtils.isEmpty(passwordTxt.getText().toString())) {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                username = usernameTxt.getText().toString().trim();
                password = passwordTxt.getText().toString().trim();

                EventBus.getDefault().post(new UserBus.LoginEvent(username, password));
            }
        });
        return view;
    }

    @Subscribe
    public void onEvent(UserBus.LoginResult loginResult) {
        EventBus.getDefault().post(new UserBus.Notify());
        if (loginResult.error) {
            Toast.makeText(getContext(), loginResult.message, Toast.LENGTH_LONG).show();
        }
    }
}
