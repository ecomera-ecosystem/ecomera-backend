package com.youssef.ecomera.common.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for sanitizing input strings to prevent XSS attacks.
 */
@UtilityClass
public class SanitizationUtils {

    /**
     * Sanitizes a string by escaping special HTML characters to prevent XSS attacks.
     */
    public String sanitize(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("*", "&#x2A;")
                .replace("'", "&#x27;");
    }
}
