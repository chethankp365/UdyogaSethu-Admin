package com.udyogasethu.admin.util;

import java.util.regex.Pattern;

/**
 * Defensive input validation and sanitization utility for administrator workflows.
 */
public final class InputSanitizer {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{6,14}$"
    );

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private InputSanitizer() {}

    /**
     * Sanitizes general text by stripping script blocks, tags, and trimming.
     */
    public static String sanitizeText(String input, int maxLength) {
        if (input == null) return "";
        // Strip script tags and their inner contents
        String sanitized = input.replaceAll("(?is)<script.*?>.*?</script>", "");
        // Strip any remaining HTML tags
        sanitized = sanitized.replaceAll("<[^>]*>", "").trim();
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }
        return sanitized;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        return trimmed.length() <= 255 && EMAIL_PATTERN.matcher(trimmed).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String trimmed = phone.trim().replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(trimmed).matches();
    }

    public static boolean isValidUuid(String uuid) {
        if (uuid == null) return false;
        return UUID_PATTERN.matcher(uuid.trim()).matches();
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasDigit = false;
        boolean hasUpper = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isUpperCase(c)) hasUpper = true;
        }
        return hasDigit && hasUpper;
    }
}
