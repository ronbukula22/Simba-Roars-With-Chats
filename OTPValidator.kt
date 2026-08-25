package com.example.simbachat

object OTPValidator {
    /**
     * Checks if the entered OTP code has the correct length (7 digits).
     */
    fun isOtpLengthCorrect(enteredCode: String): Boolean {
        return enteredCode.length == 7
    }

    /**
     * Checks if the entered OTP code is empty.
     */
    fun isOtpEmpty(enteredCode: String): Boolean {
        return enteredCode.isEmpty()
    }
}
