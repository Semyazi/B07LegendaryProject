package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView textReenterPasswordLabel = view.findViewById(R.id.reenter_password_label);
        EditText editUsername = view.findViewById(R.id.edit_username);
        EditText editPassword = view.findViewById(R.id.edit_password);
        EditText editReenterPassword = view.findViewById(R.id.edit_reenter_password);
        Button submitButton = view.findViewById(R.id.button_submit_profile);

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int visibility = (s.length() > 0) ? View.VISIBLE : View.GONE;
                if (textReenterPasswordLabel != null) {
                    textReenterPasswordLabel.setVisibility(visibility);
                }
                if (editReenterPassword != null) {
                    editReenterPassword.setVisibility(visibility);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitButton.setOnClickListener(v -> {
            String editUserText = editUsername.getText().toString().trim();
            String editPWText = editPassword.getText().toString().trim();
            String editPWReenterText = editReenterPassword.getText().toString().trim();

            if(editUserText.isEmpty() && editPWText.isEmpty() && editPWReenterText.isEmpty()) {
                ToastUtils.showToast(getContext(), "No changes were made.");
            } else if(editPWReenterText.isEmpty() && !editPWText.isEmpty()) {
                ToastUtils.showToast(getContext(), "Please re-enter your password.");
            } else if(!editPWReenterText.equals(editPWText)) {
                ToastUtils.showToast(getContext(), "Passwords do not match.");
            } else {
                ToastUtils.showToast(getContext(), "Your changes were saved successfully.");
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        return view;
    }
}