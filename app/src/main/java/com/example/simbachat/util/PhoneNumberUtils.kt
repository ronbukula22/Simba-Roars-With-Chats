
package com.example.simbachat.util

object PhoneNumberUtils {

    fun toE164(
        raw: String,
        defaultCountryCode: String = "27"
    ): String? {

        val trimmed = raw.trim()

        if (trimmed.isBlank()) return null

        var digits = trimmed.filter { it.isDigit() }

        if (digits.isBlank()) return null

        when {
            trimmed.startsWith("00") -> {
                digits = digits.drop(2)
            }

            trimmed.startsWith("+") -> {
                // Already in international format
            }

            digits.startsWith(defaultCountryCode) &&
                    digits.length in 11..15 -> {
                // Already includes country code
            }

            digits.startsWith("0") &&
                    digits.length in 9..11 -> {
                digits = defaultCountryCode + digits.drop(1)
            }

            digits.length in 7..9 -> {
                digits = defaultCountryCode + digits
            }
        }

        if (digits.length !in 8..15) return null

        return "+$digits"
    }
}
