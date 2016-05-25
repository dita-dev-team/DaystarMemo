package com.dev.dita.daystarmemo.ui.welcome;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.controller.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    /**
     * The Username txt.
     */
    @BindView(R.id.register_username)
    TextInputEditText usernameTxt;
    /**
     * The Email txt.
     */
    @BindView(R.id.register_email)
    TextInputEditText emailTxt;
    /**
     * The Password txt.
     */
    @BindView(R.id.register_pass)
    TextInputEditText passwordTxt;
    /**
     * The Password confirm txt.
     */
    @BindView(R.id.register_pass_confirm)
    TextInputEditText passwordConfirmTxt;

    /**
     * The Username.
     */
    String username;
    /**
     * The Email.
     */
    String email;
    /**
     * The Password.
     */
    String password;

    /**
     * Instantiates a new Register fragment.
     */
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    /**
     * Register.
     */
    @OnClick(R.id.register_submit_button)
    public void register() {
        UIUtils.hideKeyboard(getActivity());
        // Verify all input has been provided
        if (TextUtils.isEmpty(usernameTxt.getText().toString()) ||
                TextUtils.isEmpty(passwordTxt.getText().toString()) ||
                TextUtils.isEmpty(passwordConfirmTxt.getText().toString()) ||
                TextUtils.isEmpty(emailTxt.getText().toString())) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordTxt.getText().toString().equals(passwordConfirmTxt.getText().toString())) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        username = usernameTxt.getText().toString().trim();
        password = passwordTxt.getText().toString();
        email = emailTxt.getText().toString().trim();

        // Post a register event
        EventBus.getDefault().post(new UserBus.RegisterEvent(username, email, password));
    }

    /**
     * On event.
     *
     * @param registerResult the register result
     */
    @Subscribe
    public void onEvent(UserBus.RegisterResult registerResult) {
        EventBus.getDefault().post(new UserBus.Notify());
        if (registerResult.error) {
            Toast.makeText(getContext(), registerResult.message, Toast.LENGTH_LONG).show();
        } else {
            ((WelcomeActivity) getActivity()).showLoginView(null);
            Toast.makeText(getContext(), "Please login", Toast.LENGTH_LONG).show();
        }
    }
}
