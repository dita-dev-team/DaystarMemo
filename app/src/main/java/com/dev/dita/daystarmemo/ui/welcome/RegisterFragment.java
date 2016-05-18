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

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    EditText usernameTxt;
    EditText emailTxt;
    EditText passwordTxt;
    EditText passwordConfirmTxt;
    Button submitBtn;

    String username;
    String email;
    String password;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public void init(View view) {
        usernameTxt = (EditText) view.findViewById(R.id.register_username);
        emailTxt = (EditText) view.findViewById(R.id.register_email);
        passwordTxt = (EditText) view.findViewById(R.id.register_pass);
        passwordConfirmTxt = (EditText) view.findViewById(R.id.register_pass_confirm);
        submitBtn = (Button) view.findViewById(R.id.register_submit_button);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        init(view);
        EventBus.getDefault().register(this);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hideKeyboard(getActivity());
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

                EventBus.getDefault().post(new UserBus.RegisterEvent(username, email, password));
            }
        });
        return view;
    }

    @Subscribe
    public void onEvent(UserBus.RegisterResult registerResult) {
        EventBus.getDefault().post(new UserBus.Notify());
        if (registerResult.error) {
            Toast.makeText(getContext(), registerResult.message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
