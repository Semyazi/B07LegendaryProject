package com.professional.b07legendaryproject2026.auth;

public class SignupContract {
    public interface View{
        void showEmailError(String message);
        void showUsernameError(String message);
        void showPasswordError(String message);
        void showConfirmPasswordError(String message);
        void showSignupSuccess();
        void showSignupFailure(String message);
    }
    public interface Model{
        void signup(String email, String username, String password, SignupCallback callback);
    }
    public interface SignupCallback{
        void onSuccess();
        void onFailure(String message);
    }

}
