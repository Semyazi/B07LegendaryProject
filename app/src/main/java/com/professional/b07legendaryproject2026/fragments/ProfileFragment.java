package com.professional.b07legendaryproject2026.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
        View layoutReenterPassword = view.findViewById(R.id.layout_reenter_password);
        EditText confirmCurrentPassword = view.findViewById(R.id.confirm_current_password);
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
                if (layoutReenterPassword != null) layoutReenterPassword.setVisibility(visibility);
                if (layoutRequirements != null) layoutRequirements.setVisibility(visibility);

                if (!password.isEmpty()) {
                    validatePassword(password, reqLength, reqUppercase, reqLowercase, reqDigit, reqSpecial);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        submitButton.setOnClickListener(v -> {
            String confirmCurrentPasswordText = confirmCurrentPassword.getText().toString().trim();
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

            UserSession.getInstance().reauthenticate(confirmCurrentPasswordText, () -> {
                Runnable onAllSuccess = () -> {
                    if (getContext() != null) {
                        ToastUtils.showToast(getContext(), "Your changes were saved successfully.");
                    }

                    editPassword.setText("");
                    editReenterPassword.setText("");
                    confirmCurrentPassword.setText("");

                    if (hasUsernameChange) {
                        editUsername.setHint(editUserText);
                        editUsername.setText("");
                    }

                    submitButton.setEnabled(true);
                };

                Runnable onFailure = () -> {
                    if (getContext() != null) {
                        ToastUtils.showToast(getContext(), "Failed to save changes. Please try again.");
                    }
                    submitButton.setEnabled(true);
                };

                if (hasUsernameChange && hasPasswordChange) {
                    UserSession.getInstance().updateUsername(requireContext(), editUserText, () -> {
                        UserSession.getInstance().updatePassword(requireContext(), editPWText, onAllSuccess, null, onFailure);
                    }, onFailure);
                } else if (hasUsernameChange) {
                    UserSession.getInstance().updateUsername(requireContext(), editUserText, onAllSuccess, onFailure);
                } else {
                    UserSession.getInstance().updatePassword(requireContext(), editPWText, onAllSuccess, null, onFailure);
                }
            }, () -> {
                ToastUtils.showToast(getContext(), "Current password is incorrect.");
                submitButton.setEnabled(true);
            });
        });

        setupPasswordToggles(view);

        return view;
    }

    private void setupPasswordToggles(View view) {
        EditText editPassword = view.findViewById(R.id.edit_password);
        EditText editReenterPassword = view.findViewById(R.id.edit_reenter_password);
        EditText confirmCurrentPassword = view.findViewById(R.id.confirm_current_password);
        
        ImageButton toggleNew = view.findViewById(R.id.toggleNewPassword);
        ImageButton toggleConfirm = view.findViewById(R.id.toggleConfirmPassword);
        ImageButton toggleCurrent = view.findViewById(R.id.toggleCurrentPassword);

        if (toggleNew != null) toggleNew.setOnClickListener(v -> toggleVisibility(editPassword, toggleNew));
        if (toggleConfirm != null) toggleConfirm.setOnClickListener(v -> toggleVisibility(editReenterPassword, toggleConfirm));
        if (toggleCurrent != null) toggleCurrent.setOnClickListener(v -> toggleVisibility(confirmCurrentPassword, toggleCurrent));
    }

    private void toggleVisibility(EditText editText, ImageButton button) {
        if (editText == null || button == null) return;
        int selection = editText.getSelectionEnd();
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            button.setImageResource(R.drawable.ic_visibility);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            button.setImageResource(R.drawable.ic_visibility_off);
        }
        editText.setSelection(selection);
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