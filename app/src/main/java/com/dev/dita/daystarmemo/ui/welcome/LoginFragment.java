package com.dev.dita.daystarmemo.ui.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dev.dita.daystarmemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {
    @BindView(R.id.login_username)
    EditText usernameTxt;
    @BindView(R.id.login_pass)
    EditText passwordTxt;

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

        return view;
    }

    @OnClick(R.id.login_submit_button)
    public void login(View view) {
        System.out.println("Login button clicked");
    }
}
