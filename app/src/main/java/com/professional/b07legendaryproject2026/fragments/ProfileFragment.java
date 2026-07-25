package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.utils.ToastUtils;

public class ProfileFragment extends Fragment {

    private boolean isPwValid = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView textReenterPasswordLabel = view.findViewById(R.id.reenter_password_label);
        EditText editUsername = view.findViewById(R.id.edit_username);
        EditText editPassword = view.findViewById(R.id.edit_password);
        EditText editReenterPassword = view.findViewById(R.id.edit_reenter_password);
        Button submitButton = view.findViewById(R.id.button_submit_profile);

        LinearLayout layoutRequirements = view.findViewById(R.id.layout_password_requirements);
        TextView reqLength = view.findViewById(R.id.req_length);
        TextView reqUppercase = view.findViewById(R.id.req_uppercase);
        TextView reqLowercase = view.findViewById(R.id.req_lowercase);
        TextView reqDigit = view.findViewById(R.id.req_digit);
        TextView reqSpecial = view.findViewById(R.id.req_special);

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                int visibility = (!password.isEmpty()) ? View.VISIBLE : View.GONE;
                
                if (textReenterPasswordLabel != null) textReenterPasswordLabel.setVisibility(visibility);
                if (editReenterPassword != null) editReenterPassword.setVisibility(visibility);
                if (layoutRequirements != null) layoutRequirements.setVisibility(visibility);

                if (!password.isEmpty()) {
                    validatePassword(password, reqLength, reqUppercase, reqLowercase, reqDigit, reqSpecial);
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
            } else if(!editPWText.isEmpty() && !isPwValid) {
                ToastUtils.showToast(getContext(), "Please fulfill all password requirements.");
            } else if(!editPWText.isEmpty() && editPWReenterText.isEmpty()) {
                ToastUtils.showToast(getContext(), "Please re-enter your password.");
            } else if(!editPWText.isEmpty() && !editPWReenterText.equals(editPWText)) {
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

    private void validatePassword(String password, TextView reqLength, TextView reqUppercase, TextView reqLowercase, TextView reqDigit, TextView reqSpecial) {
        boolean hasLength = password.length() >= 8;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()].*");
        
        updateRequirementUI(reqLength, hasLength);
        updateRequirementUI(reqUppercase, hasUpper);
        updateRequirementUI(reqLowercase, hasLower);
        updateRequirementUI(reqDigit, hasDigit);
        updateRequirementUI(reqSpecial, hasSpecial);

        isPwValid = hasLength && hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void updateRequirementUI(TextView textView, boolean isMet) {
        if (textView == null || getContext() == null) return;
        int color = ContextCompat.getColor(getContext(), isMet ? R.color.success_green : R.color.imperial_red);
        textView.setTextColor(color);
    }
}
