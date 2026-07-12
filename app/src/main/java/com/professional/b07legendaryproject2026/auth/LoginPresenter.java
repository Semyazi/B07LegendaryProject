package com.professional.b07legendaryproject2026.auth;

public class LoginPresenter {
    private LoginContract.View view;
    private LoginContract.Model model;
    public LoginPresenter(LoginContract.View view, LoginContract.Model model){
        this.view = view;
        this.model = model;
    }
    public void login(String email, String password){
        if(email == null || email.trim().isEmpty()){
            view.showEmailError("Email cannot be empty");
            return;
        }
        if(password == null || password.trim().isEmpty()){
            view.showPasswordError("Password cannot be empty");
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


