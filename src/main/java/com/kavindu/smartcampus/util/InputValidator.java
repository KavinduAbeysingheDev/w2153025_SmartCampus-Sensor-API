package com.kavindu.smartcampus.util;

import com.kavindu.smartcampus.exception.InvalidInputException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class InputValidator {

    private static final Set<String> ALLOWED_STATUSES =
            new HashSet<>(Arrays.asList("ACTIVE", "MAINTENANCE", "OFFLINE"));

    private InputValidator() {
    }

    public static String required(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " is required.");
        }
        return value.trim();
    }

    public static void positiveCapacity(int capacity) {
        if (capacity <= 0) {
            throw new InvalidInputException("capacity must be greater than zero.");
        }
    }

    public static String normalizeStatus(String value) {
        String normalized = required(value, "status").toUpperCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new InvalidInputException("status must be one of ACTIVE, MAINTENANCE, OFFLINE.");
        }
        return normalized;
    }

    public static String normalizeType(String type) {
        return required(type, "type");
    }

    public static long normalizeTimestamp(long timestamp) {
        return timestamp <= 0 ? System.currentTimeMillis() : timestamp;
    }
}
