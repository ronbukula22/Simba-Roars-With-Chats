package com.example.simbachat

import com.example.simbachat.auth.OTPValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OTPValidatorTest {

    @Test
    fun isOtpLengthCorrect_returnsTrueForSevenDigits() {
        val code = "1234567"
        assertTrue(OTPValidator.isOtpLengthCorrect(code))
    }

    @Test
    fun isOtpLengthCorrect_returnsFalseForShortCode() {
        val code = "123456"
        assertFalse(OTPValidator.isOtpLengthCorrect(code))
    }

    @Test
    fun isOtpLengthCorrect_returnsFalseForLongCode() {
        val code = "12345678"
        assertFalse(OTPValidator.isOtpLengthCorrect(code))
    }

    @Test
    fun isOtpEmpty_returnsTrueForEmptyString() {
        val code = ""
        assertTrue(OTPValidator.isOtpEmpty(code))
    }

    @Test
    fun isOtpEmpty_returnsFalseForNonEmptyString() {
        val code = "1"
        assertFalse(OTPValidator.isOtpEmpty(code))
    }
}
