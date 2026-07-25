package com.professional.b07legendaryproject2026.utils;

public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;

    private ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult error(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}