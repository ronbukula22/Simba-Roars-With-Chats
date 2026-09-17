package com.example.simbachat.util

object OTPValidator {
    fun isOtpLengthCorrect(enteredCode: String): Boolean =
        enteredCode.matches(Regex("\\d{7}"))

    fun isOtpEmpty(enteredCode: String): Boolean =
        enteredCode.isBlank()
}
