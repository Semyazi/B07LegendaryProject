package com.professional.b07legendaryproject2026;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.professional.b07legendaryproject2026.auth.LoginContract;
import com.professional.b07legendaryproject2026.auth.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginContract.View{
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button createAccountButton;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.loginEmailInput);
        passwordInput = findViewById(R.id.loginPasswordInput);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        presenter = new LoginPresenter(this, new TemporaryLoginModel());

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            presenter.login(email, password);
        });
        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void showEmailError(String message){
        emailInput.setError(message);
    }

    @Override
    public void showPasswordError(String message){
        emailInput.setError(message);
    }

    @Override
    public void showLoginSuccess(){
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoginFailure(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static class TemporaryLoginModel implements LoginContract.Model{
        @Override
        public void login(String email, String password, LoginContract.LoginCallback callback){
            callback.onSuccess();
        }
    }
}