package com.professional.b07legendaryproject2026.utils;

public abstract class BaseValidator {
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}