package com.professional.b07legendaryproject2026;
import com.professional.b07legendaryproject2026.auth.LoginContract;
import com.professional.b07legendaryproject2026.auth.LoginPresenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

public class LoginPresenterTest {

    @Mock
    private LoginContract.View view;

    @Mock
    private LoginContract.Model model;

    private LoginPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        presenter = new LoginPresenter(view, model);
    }

    @Test
    public void login_emptyEmail_showsEmailError() {
        presenter.login("", "password123");

        verify(view).showEmailError("Email cannot be empty.");
        verify(model, never()).login(any(), any(), any());
    }

    @Test
    public void login_invalidEmailFormat_showsEmailError() {
        presenter.login("not-an-email", "password123");
        verify(view).showEmailError("Invalid email format (e.g., name@example.com).");
        verify(model, never()).login(any(), any(), any());
    }

    @Test
    public void login_emptyPassword_showsPasswordError() {
        presenter.login("test@example.com", "");

        verify(view).showPasswordError("Password cannot be empty.");
        verify(model, never()).login(any(), any(), any());
    }

    @Test
    public void login_validInput_callsModelLogin() {
        presenter.login("test@example.com", "password123");

        verify(model).login(
                eq("test@example.com"),
                eq("password123"),
                any(LoginContract.LoginCallback.class)
        );
    }

    @Test
    public void login_modelSuccess_showsLoginSuccess() {
        presenter.login("test@example.com", "password123");

        ArgumentCaptor<LoginContract.LoginCallback> callbackCaptor =
                ArgumentCaptor.forClass(LoginContract.LoginCallback.class);

        verify(model).login(
                eq("test@example.com"),
                eq("password123"),
                callbackCaptor.capture()
        );

        callbackCaptor.getValue().onSuccess();

        verify(view).showLoginSuccess();
    }

    @Test
    public void login_modelFailure_showsLoginFailure() {
        presenter.login("wrong@example.com", "wrongpassword");

        ArgumentCaptor<LoginContract.LoginCallback> callbackCaptor =
                ArgumentCaptor.forClass(LoginContract.LoginCallback.class);

        verify(model).login(
                eq("wrong@example.com"),
                eq("wrongpassword"),
                callbackCaptor.capture()
        );

        callbackCaptor.getValue().onFailure("Invalid email or password.");

        verify(view).showLoginFailure("Invalid email or password.");
    }
}