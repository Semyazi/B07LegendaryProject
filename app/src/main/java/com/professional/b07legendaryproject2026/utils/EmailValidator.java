package com.professional.b07legendaryproject2026.utils;

public class EmailValidator extends BaseValidator {
    private final static String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static ValidationResult validate(String email) {
        if (isEmpty(email)) {
            return ValidationResult.error("Email cannot be empty.");
        }
        if (!email.matches(emailRegex)) {
            return ValidationResult.error("Invalid email format (e.g., name@example.com).");
        }
        return ValidationResult.ok();
    }
}