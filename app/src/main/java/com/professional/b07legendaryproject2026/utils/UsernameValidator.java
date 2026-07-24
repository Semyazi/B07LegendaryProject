package com.professional.b07legendaryproject2026.utils;

public class UsernameValidator extends BaseValidator {
    public static ValidationResult validate(String username) {
        if (isEmpty(username)) {
            return ValidationResult.error("Username cannot be empty.");
        }
        return ValidationResult.ok();
    }
}