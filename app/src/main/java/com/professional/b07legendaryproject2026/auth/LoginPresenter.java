package com.professional.b07legendaryproject2026.auth;

import com.professional.b07legendaryproject2026.utils.EmailValidator;
import com.professional.b07legendaryproject2026.utils.PasswordValidator;
import com.professional.b07legendaryproject2026.utils.ValidationResult;

public class LoginPresenter {
    private LoginContract.View view;
    private LoginContract.Model model;
    public LoginPresenter(LoginContract.View view, LoginContract.Model model){
        this.view = view;
        this.model = model;
    }

    public void login(String email, String password) {
        ValidationResult emailResult = EmailValidator.validate(email);
        if (!emailResult.isValid()) {
            view.showEmailError(emailResult.getErrorMessage());
            return;
        }

        if (PasswordValidator.isEmpty(password)) {
            view.showPasswordError("Password cannot be empty.");
            return;
        }

        model.login(email, password, new LoginContract.LoginCallback(){
            @Override
            public void onSuccess(){
                view.showLoginSuccess();
            }
            @Override
            public void onFailure(String message){
                view.showLoginFailure(message);
            }
        });
    }
}