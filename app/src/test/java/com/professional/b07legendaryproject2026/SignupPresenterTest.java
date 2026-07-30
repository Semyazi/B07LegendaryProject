package com.professional.b07legendaryproject2026;

import com.professional.b07legendaryproject2026.auth.SignupContract;
import com.professional.b07legendaryproject2026.auth.SignupPresenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SignupPresenterTest {
    @Mock
    private SignupContract.View view;

    @Mock
    private SignupContract.Model model;
    private SignupPresenter presenter;

    @Before
    public void setup(){
        MockitoAnnotations.openMocks(this);
        presenter = new SignupPresenter(view, model);
    }

    @Test
    public void signup_emptyEmail_showsEmailError() {
        presenter.signup("", "testuser", "Password123!", "Password123!");
        verify(view).showEmailError("Email cannot be empty.");
        verify(model, never()).signup(any(), any(), any(), any());
    }

    @Test
    public void signup_invalidEmail_showsEmailError() {
        presenter.signup("bad-email", "testuser", "Password123!", "Password123!");
        verify(view).showEmailError("Invalid email format (e.g., name@example.com).");
        verify(model, never()).signup(any(), any(), any(), any());
    }

    @Test
    public void signup_emptyUsername_showsUsernameError(){
        presenter.signup("test@example.com", "", "Password123!", "Password123!");
        verify(view).showUsernameError("Username cannot be empty.");
        verify(model, never()).signup(any(), any(), any(), any());
    }

    @Test
    public void signup_emptyPassword_showsPasswordError(){
        presenter.signup("test@example.com", "testuser", "", "password123");
        verify(view).showPasswordError("Password cannot be empty.");
        verify(model, never()).signup(any(),any(), any(), any());
    }

    @Test
    public void signup_emptyConfirmPassword_showsConfirmPasswordError(){
        presenter.signup("test@example.com","testuser", "Password123!", "");
        verify(view).showConfirmPasswordError("Confirm password cannot be empty.");
        verify(model, never()).signup(any(), any(),any(), any());
    }

    @Test
    public void signup_passwordsDoNotMatch_showsConfirmPasswordError(){
        presenter.signup("test@example.com","testuser", "Password123!", "Password321!");
        verify(view).showConfirmPasswordError("Passwords do not match.");
        verify(model, never()).signup(any(), any(),any(), any());
    }

    @Test
    public void signup_shortPassword_showsPasswordError() {
        presenter.signup("test@example.com","testuser", "Pass1!", "Pass1!");
        verify(view).showPasswordError("Password must be at least 8 characters long.");
        verify(model, never()).signup(any(),any(), any(), any());
    }

    @Test
    public void signup_noUppercasePassword_showsPasswordError() {
        presenter.signup("test@example.com","testuser", "password123!", "password123!");
        verify(view).showPasswordError("Password must contain at least one uppercase letter.");
        verify(model, never()).signup(any(),any(), any(), any());
    }

    @Test
    public void signup_noLowercasePassword_showsPasswordError() {
        presenter.signup("test@example.com","testuser", "PASSWORD123!", "PASSWORD123!");
        verify(view).showPasswordError("Password must contain at least one lowercase letter.");
        verify(model, never()).signup(any(),any(), any(), any());
    }

    @Test
    public void signup_noDigitPassword_showsPasswordError() {
        presenter.signup("test@example.com","testuser", "Password!!!!!", "Password!!!!!");
        verify(view).showPasswordError("Password must contain at least one number.");
        verify(model, never()).signup(any(),any(), any(), any());
    }

    @Test
    public void signup_noSpecialCharPassword_showsPasswordError() {
        presenter.signup("test@example.com","testuser", "Password12345", "Password12345");
        verify(view).showPasswordError("Password must contain a special character.");
        verify(model, never()).signup(any(),any(), any(), any());
    }

    @Test
    public void signup_validInput_callsModelSignup(){
        presenter.signup("test@example.com","testuser", "Password123!", "Password123!");
        verify(model).signup(
                eq("test@example.com"),
                eq("testuser"),
                eq("Password123!"),
                any(SignupContract.SignupCallback.class)
        );
    }

    @Test
    public void signup_modelSuccess_showsSignupSuccess() {
        presenter.signup("test@example.com","testuser", "Password123!", "Password123!");
        ArgumentCaptor<SignupContract.SignupCallback> callbackCaptor = ArgumentCaptor.forClass(SignupContract.SignupCallback.class);
        verify(model).signup(
                eq("test@example.com"),
                eq("testuser"),
                eq("Password123!"),
                callbackCaptor.capture()
        );
        callbackCaptor.getValue().onSuccess();
        verify(view).showSignupSuccess();
    }

    @Test
    public void signup_modelFailure_showSignupFailure(){
        presenter.signup("test@example.com","testuser", "Password123!", "Password123!");
        ArgumentCaptor<SignupContract.SignupCallback> callbackCaptor = ArgumentCaptor.forClass(SignupContract.SignupCallback.class);
        verify(model).signup(
                eq("test@example.com"),
                eq("testuser"),
                eq("Password123!"),
                callbackCaptor.capture());
        callbackCaptor.getValue().onFailure("Signup failed");
        verify(view).showSignupFailure("Signup failed");
    }

}
