package com.professional.b07legendaryproject2026;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.professional.b07legendaryproject2026.auth.FirebaseAuthModel;
import com.professional.b07legendaryproject2026.auth.SignupContract;
import com.professional.b07legendaryproject2026.auth.SignupPresenter;

public class SignupActivity extends AppCompatActivity implements SignupContract.View{
    private EditText emailInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button signupButton;
    private Button backToLoginButton;
    private SignupPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailInput = findViewById(R.id.signupEmailInput);
        usernameInput = findViewById(R.id.signupUsernameInput);
        passwordInput = findViewById(R.id.signupPasswordInput);
        confirmPasswordInput = findViewById(R.id.signupConfirmPasswordInput);
        signupButton = findViewById(R.id.signupButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        presenter = new SignupPresenter(this, new FirebaseAuthModel(this));

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            presenter.signup(email, username, password, confirmPassword);
        });
        backToLoginButton.setOnClickListener(v -> finish());
        
        setupPasswordToggles();
    }

    private void setupPasswordToggles() {
        ImageButton togglePw = findViewById(R.id.toggleSignupPassword);
        ImageButton toggleConfirm = findViewById(R.id.toggleSignupConfirmPassword);
        
        if (togglePw != null) {
            togglePw.setOnClickListener(v -> toggleVisibility(passwordInput, togglePw));
        }
        if (toggleConfirm != null) {
            toggleConfirm.setOnClickListener(v -> toggleVisibility(confirmPasswordInput, toggleConfirm));
        }
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

    @Override
    public void showEmailError(String message){
        emailInput.setError(message);
    }
    @Override
    public void showUsernameError(String message) { usernameInput.setError(message); }
    @Override
    public void showPasswordError(String message){
        passwordInput.setError(message);
    }

    @Override
    public void showConfirmPasswordError(String message){
        confirmPasswordInput.setError(message);
    }

    @Override
    public void showSignupSuccess(){
        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showSignupFailure(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}