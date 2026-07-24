package com.professional.b07legendaryproject2026.utils;

public class PasswordValidator extends BaseValidator {
    public static boolean hasMinLength(String password) { return password != null && password.length() >= 8; }
    public static boolean hasUppercase(String password) { return password != null && password.matches(".*[A-Z].*"); }
    public static boolean hasLowercase(String password) { return password != null && password.matches(".*[a-z].*"); }
    public static boolean hasDigit(String password) { return password != null && password.matches(".*\\d.*"); }
    public static boolean hasSpecialChar(String password) { return password != null && password.matches(".*[!@#$%^&*()].*"); }

    public static ValidationResult validate(String password) {
        if (isEmpty(password)) {
            return ValidationResult.error("Password cannot be empty.");
        }
        if (!hasMinLength(password)) {
            return ValidationResult.error("Password must be at least 8 characters long.");
        }
        if (!hasUppercase(password)) {
            return ValidationResult.error("Password must contain at least one uppercase letter.");
        }
        if (!hasLowercase(password)) {
            return ValidationResult.error("Password must contain at least one lowercase letter.");
        }
        if (!hasDigit(password)) {
            return ValidationResult.error("Password must contain at least one number.");
        }
        if (!hasSpecialChar(password)) {
            return ValidationResult.error("Password must contain a special character.");
        }
        return ValidationResult.ok();
    }
}