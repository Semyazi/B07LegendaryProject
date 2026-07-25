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
import android.content.Intent;

import com.professional.b07legendaryproject2026.LoginActivity;
import com.professional.b07legendaryproject2026.R;
import com.professional.b07legendaryproject2026.managers.UserSession;
import com.professional.b07legendaryproject2026.utils.PasswordValidator;
import com.professional.b07legendaryproject2026.utils.ToastUtils;
import com.professional.b07legendaryproject2026.utils.UsernameValidator;
import com.professional.b07legendaryproject2026.utils.ValidationResult;

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
        Button cancelButton = view.findViewById(R.id.button_cancel_profile);

        if (cancelButton != null) {
            cancelButton.setOnClickListener(v -> {
                if (getActivity() != null && isAdded()) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        LinearLayout layoutRequirements = view.findViewById(R.id.layout_password_requirements);
        TextView reqLength = view.findViewById(R.id.req_length);
        TextView reqUppercase = view.findViewById(R.id.req_uppercase);
        TextView reqLowercase = view.findViewById(R.id.req_lowercase);
        TextView reqDigit = view.findViewById(R.id.req_digit);
        TextView reqSpecial = view.findViewById(R.id.req_special);

        String currentUsername = UserSession.getInstance().getUsername();
        if (currentUsername != null && !currentUsername.isEmpty() && editUsername != null) {
            editUsername.setHint(currentUsername);
        }

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

            boolean hasUsernameChange = !editUserText.isEmpty() && !editUserText.equals(currentUsername);
            boolean hasPasswordChange = !editPWText.isEmpty();

            if (!hasUsernameChange && !hasPasswordChange) {
                ToastUtils.showToast(getContext(), "No changes were made.");
                return;
            }

            if (hasUsernameChange) {
                ValidationResult userResult = UsernameValidator.validate(editUserText);
                if (!userResult.isValid()) {
                    ToastUtils.showToast(getContext(), userResult.getErrorMessage());
                    return;
                }
            }

            if (hasPasswordChange) {
                ValidationResult pwResult = PasswordValidator.validate(editPWText);
                if (!pwResult.isValid()) {
                    ToastUtils.showToast(getContext(), pwResult.getErrorMessage());
                    return;
                }
                if (editPWReenterText.isEmpty()) {
                    ToastUtils.showToast(getContext(), "Please re-enter your password.");
                    return;
                }
                if (!editPWReenterText.equals(editPWText)) {
                    ToastUtils.showToast(getContext(), "Passwords do not match.");
                    return;
                }
            }

            submitButton.setEnabled(false);
            cancelButton.setEnabled(false);

            Runnable onAllSuccess = () -> {
                if (getContext() != null) {
                    ToastUtils.showToast(getContext(), "Your changes were saved successfully.");
                }
                if (getActivity() != null && isAdded()) {
                    getParentFragmentManager().popBackStack();
                }
            };

            Runnable onReauthRequired = () -> {
                if (getContext() != null) {
                    ToastUtils.showToast(getContext(), "Session expired. Please log back in with your current password.");
                }

                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                if (getActivity() != null) {
                    getActivity().finish();
                }
            };

            Runnable onFailure = () -> {
                if (getContext() != null) {
                    ToastUtils.showToast(getContext(), "Failed to save changes. Please try again.");
                }
                submitButton.setEnabled(true);
                cancelButton.setEnabled(true);
            };

            if (hasUsernameChange && hasPasswordChange) {
                UserSession.getInstance().updateUsername(requireContext(), editUserText, () -> {
                    UserSession.getInstance().updatePassword(requireContext(), editPWText, onAllSuccess, onReauthRequired, onFailure);
                }, onFailure);
            } else if (hasUsernameChange) {
                UserSession.getInstance().updateUsername(requireContext(), editUserText, onAllSuccess, onFailure);
            } else {
                UserSession.getInstance().updatePassword(requireContext(), editPWText, onAllSuccess, onReauthRequired, onFailure);
            }
        });

        return view;
    }

    private void validatePassword(String password, TextView reqLength, TextView reqUppercase, TextView reqLowercase, TextView reqDigit, TextView reqSpecial) {
        updateRequirementUI(reqLength, PasswordValidator.hasMinLength(password));
        updateRequirementUI(reqUppercase, PasswordValidator.hasUppercase(password));
        updateRequirementUI(reqLowercase, PasswordValidator.hasLowercase(password));
        updateRequirementUI(reqDigit, PasswordValidator.hasDigit(password));
        updateRequirementUI(reqSpecial, PasswordValidator.hasSpecialChar(password));
    }

    private void updateRequirementUI(TextView textView, boolean isMet) {
        if (textView == null || getContext() == null) return;
        int color = ContextCompat.getColor(getContext(), isMet ? R.color.success_green : R.color.imperial_red);
        textView.setTextColor(color);
    }
}