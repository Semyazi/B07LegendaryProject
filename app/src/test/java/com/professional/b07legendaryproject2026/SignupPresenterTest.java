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
    public void signup_emptyEmail_showsEmailError(){
        presenter.signup("","password123", "password123");
        verify(view).showEmailError("Email cannot be empty");
        verify(model, never()).signup(any(), any(),any());
    }

    @Test
    public void signup_emptyPassword_showsPasswordError(){
        presenter.signup("test@example.com", "", "password123");
        verify(view).showPasswordError("Password cannot be empty");
        verify(model, never()).signup(any(), any(), any());
    }

    @Test
    public void signup_emptyConfirmPassword_showsConfirmPasswordError(){
        presenter.signup("test@example.com", "password123", "");
        verify(view).showConfirmPasswordError("Confirm password cannot be empty");
        verify(model, never()).signup(any(), any(), any());
    }

    @Test
    public void signup_passwordsDoNotMatch_showsConfirmPasswordError(){
        presenter.signup("test@example.com", "password123", "password321");
        verify(view).showConfirmPasswordError("Passwords do not match");
        verify(model, never()).signup(any(), any(), any());
    }

    @Test
    public void signup_validInput_callsModelSignup(){
        presenter.signup("test@example.com", "password123", "password123");
        verify(model).signup(
                eq("test@example.com"),
                eq("password123"),
                any(SignupContract.SignupCallback.class)
        );
    }

    @Test
    public void signup_modelSuccess_showsSignupSuccess() {
        presenter.signup("test@example.com", "password123", "password123");
        ArgumentCaptor<SignupContract.SignupCallback> callbackCaptor = ArgumentCaptor.forClass(SignupContract.SignupCallback.class);
        verify(model).signup(
                eq("test@example.com"),
                eq("password123"),
                callbackCaptor.capture()
        );
        callbackCaptor.getValue().onSuccess();
        verify(view).showSignupSuccess();
    }

    @Test
    public void signup_modelFailure_showSignupFailure(){
        presenter.signup("test@example.com", "password123", "password123");
        ArgumentCaptor<SignupContract.SignupCallback> callbackCaptor = ArgumentCaptor.forClass(SignupContract.SignupCallback.class);
        verify(model).signup(
                eq("test@example.com"),
                eq("password123"),
                callbackCaptor.capture());
        callbackCaptor.getValue().onFailure("Signup failed");
        verify(view).showSignupFailure("Signup failed");
    }

}
