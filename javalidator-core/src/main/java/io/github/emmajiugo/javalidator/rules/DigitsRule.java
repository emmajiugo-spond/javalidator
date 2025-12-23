package io.github.emmajiugo.javalidator.rules;

import io.github.emmajiugo.javalidator.ValidationRule;

/**
 * Validation rule that checks if a value has a specific number of digits.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code @Rule("digits:5")} - exactly 5 digits</li>
 *   <li>{@code @Rule("digits:3,5")} - between 3 and 5 digits (inclusive)</li>
 * </ul>
 *
 * <p>This rule validates that the field contains the specified number of digits.
 * It works with String, Integer, Long, and other numeric types.
 *
 * <p>Examples:
 * <ul>
 *   <li>{@code @Rule("digits:4")} ensures exactly 4 digits (like "1234" or 1234)</li>
 *   <li>{@code @Rule("digits:3,5")} ensures between 3 and 5 digits (like "123", "1234", or "12345")</li>
 * </ul>
 *
 * <p>Common use cases:
 * <ul>
 *   <li>PIN codes: {@code @Rule("digits:4")}</li>
 *   <li>ZIP codes: {@code @Rule("digits:5")}</li>
 *   <li>Verification codes: {@code @Rule("digits:6")}</li>
 *   <li>Year validation: {@code @Rule("digits:4")}</li>
 *   <li>Phone numbers: {@code @Rule("digits:10,11")}</li>
 * </ul>
 *
 * <p>Note: This rule only counts digits (0-9). Other characters like spaces,
 * hyphens, or letters will cause validation to fail.
 */
public class DigitsRule implements ValidationRule {

    @Override
    public String validate(String fieldName, Object value, String parameter) {
        // Skip validation if value is null (let 'required' rule handle nulls)
        if (value == null) {
            return null;
        }

        // Parameter is required
        if (parameter == null || parameter.isEmpty()) {
            throw new IllegalArgumentException("The 'digits' rule requires a parameter specifying the number of digits (e.g., 'digits:4' or 'digits:3,5')");
        }

        // Parse the parameter - supports both "5" (exact) and "3,5" (range) formats
        int minDigits;
        int maxDigits;

        if (parameter.contains(",")) {
            // Range format: "min,max"
            String[] parts = parameter.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("The 'digits' rule range must have exactly two values (e.g., 'digits:3,5')");
            }
            try {
                minDigits = Integer.parseInt(parts[0].trim());
                maxDigits = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The 'digits' rule parameters must be valid integers: " + parameter);
            }
            if (minDigits < 1 || maxDigits < 1) {
                throw new IllegalArgumentException("The 'digits' rule parameters must be positive integers: " + parameter);
            }
            if (minDigits > maxDigits) {
                throw new IllegalArgumentException("The 'digits' rule minimum (" + minDigits + ") cannot be greater than maximum (" + maxDigits + ")");
            }
        } else {
            // Exact format: "5"
            try {
                minDigits = Integer.parseInt(parameter.trim());
                maxDigits = minDigits;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The 'digits' rule parameter must be a valid integer: " + parameter);
            }
            if (minDigits < 1) {
                throw new IllegalArgumentException("The 'digits' rule parameter must be a positive integer: " + parameter);
            }
        }

        // Convert value to string
        String valueStr = value.toString();

        // Check if all characters are digits (no non-digit characters allowed)
        if (!valueStr.matches("\\d+")) {
            return "The " + fieldName + " must contain only digits.";
        }

        int digitCount = valueStr.length();

        // Check digit count based on format
        if (minDigits == maxDigits) {
            // Exact match required
            if (digitCount != minDigits) {
                return "The " + fieldName + " must be exactly " + minDigits + " digits.";
            }
        } else {
            // Range check
            if (digitCount < minDigits || digitCount > maxDigits) {
                return "The " + fieldName + " must be between " + minDigits + " and " + maxDigits + " digits.";
            }
        }

        return null; // Validation passes
    }

    @Override
    public String getName() {
        return "digits";
    }
}