package com.professional.b07legendaryproject2026.auth;

import android.content.Context;
import com.professional.b07legendaryproject2026.managers.UserSession;

public class FirebaseAuthModel implements LoginContract.Model, SignupContract.Model {

    private final Context context;

    public FirebaseAuthModel(Context context) {
        this.context = context;
    }

    @Override
    public void login(String email, String password, LoginContract.LoginCallback callback) {
        UserSession.getInstance().login(
                context,
                email,
                password,
                () -> callback.onSuccess(),
                () -> callback.onFailure("Login failed. Please check your credentials.")
        );
    }

    @Override
    public void signup(String email, String username, String password, SignupContract.SignupCallback callback) {
        UserSession.getInstance().signup(
                context,
                email,
                username,
                password,
                () -> callback.onSuccess(),
                () -> callback.onFailure("Signup failed. Email may already be in use.")
        );
    }
}