package com.retailetics.ezycollect.data.remote.dto

data class RegistrationRequest(
    val address: String,
    val bankAccount: String,
    val bankName: String,
    val biometricEnabled: Boolean,
    val email: String,
    val fullName: String,
    val loginPin: String,
    val password: String,
    val phone: String,
    val shopName: String,
    val termsAccepted: Boolean
)