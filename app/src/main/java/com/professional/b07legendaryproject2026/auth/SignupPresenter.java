package com.professional.b07legendaryproject2026.auth;

public class SignupPresenter {
    private SignupContract.View view;
    private SignupContract.Model model;

    private boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(pattern);
    }
    public SignupPresenter(SignupContract.View view, SignupContract.Model model){
        this.view = view;
        this.model = model;
    }
    public void signup(String email, String password, String confirmPassword){
        if(email == null || email.trim().isEmpty()){
            view.showEmailError("Email cannot be empty");
            return;
        }
        if(password == null || password.trim().isEmpty()){
            view.showPasswordError("Password cannot be empty");
            return;
        }
        if(confirmPassword == null || confirmPassword.trim().isEmpty()){
            view.showConfirmPasswordError("Confirm password cannot be empty");
            return;
        }
        if (!isStrongPassword(password)) {
            view.showPasswordError("Password must be at least 8 characters and include uppercase, lowercase, number, and special character.");
            return;
        }
        if(!password.equals(confirmPassword)){
            view.showConfirmPasswordError("Passwords do not match.");
            return;
        }
        model.signup(email, password, new SignupContract.SignupCallback(){
            @Override
            public void onSuccess(){
                view.showSignupSuccess();
            }
            @Override
            public void onFailure(String message){
                view.showSignupFailure(message);
            }

                }
        );
    }
}
