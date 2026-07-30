package com.professional.b07legendaryproject2026;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.professional.b07legendaryproject2026.auth.FirebaseAuthModel;
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

        presenter = new LoginPresenter(this, new FirebaseAuthModel(this));

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            presenter.login(email, password);
        });
        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
        setupPasswordToggle();
    }

    private void setupPasswordToggle() {
        ImageButton toggle = findViewById(R.id.togglePassword);
        EditText passwordInputView = findViewById(R.id.loginPasswordInput);
        if (toggle == null || passwordInputView == null) return;

        toggle.setOnClickListener(v -> {
            int selection = passwordInputView.getSelectionEnd();
            if (passwordInputView.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordInputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggle.setImageResource(R.drawable.ic_visibility);
            } else {
                passwordInputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggle.setImageResource(R.drawable.ic_visibility_off);
            }
            passwordInputView.setSelection(selection);
        });
    }

    @Override
    public void showEmailError(String message){
        emailInput.setError(message);
    }

    @Override
    public void showPasswordError(String message){
        passwordInput.setError(message);
    }

    @Override
    public void showLoginSuccess(){
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoginFailure(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}