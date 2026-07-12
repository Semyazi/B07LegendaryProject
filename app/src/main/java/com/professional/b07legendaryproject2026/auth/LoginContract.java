package com.professional.b07legendaryproject2026.auth;

public interface LoginContract {
    interface View{
        void showEmailError(String message);
        void showPasswordError(String message);
        void showLoginSuccess();
        void showLoginFailure(String message);
    }
    interface Model{
        void login(String email, String password, LoginCallback callback);
    }
    interface LoginCallback{
        void onSuccess();
        void onFailure(String message);
    }
}
