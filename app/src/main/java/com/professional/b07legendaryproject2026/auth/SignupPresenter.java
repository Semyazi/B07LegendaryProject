package com.professional.b07legendaryproject2026.auth;

import com.professional.b07legendaryproject2026.utils.EmailValidator;
import com.professional.b07legendaryproject2026.utils.PasswordValidator;
import com.professional.b07legendaryproject2026.utils.UsernameValidator;
import com.professional.b07legendaryproject2026.utils.ValidationResult;

public class SignupPresenter {
    private SignupContract.View view;
    private SignupContract.Model model;

    public SignupPresenter(SignupContract.View view, SignupContract.Model model){
        this.view = view;
        this.model = model;
    }

    public void signup(String email, String username, String password, String confirmPassword) {
        ValidationResult emailResult = EmailValidator.validate(email);
        if (!emailResult.isValid()) {
            view.showEmailError(emailResult.getErrorMessage());
            return;
        }

        ValidationResult usernameResult = UsernameValidator.validate(username);
        if (!usernameResult.isValid()) {
            view.showUsernameError(usernameResult.getErrorMessage());
            return;
        }

        ValidationResult passwordResult = PasswordValidator.validate(password);
        if (!passwordResult.isValid()) {
            view.showPasswordError(passwordResult.getErrorMessage());
            return;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            view.showConfirmPasswordError("Confirm password cannot be empty.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.showConfirmPasswordError("Passwords do not match.");
            return;
        }

        model.signup(email, username, password, new SignupContract.SignupCallback() {
            @Override
            public void onSuccess() { view.showSignupSuccess(); }
            @Override
            public void onFailure(String message) { view.showSignupFailure(message); }
        });
    }
}