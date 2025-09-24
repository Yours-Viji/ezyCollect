package com.retailetics.ezycollect.data.remote.dto

data class LoginResponse(
    val merchant: Merchant,
    val token: String
)
data class Merchant(
    val address: String,
    val bank_account: String,
    val biometric_enabled: Int,
    val created_at: String,
    val email: String,
    val full_name: String,
    val id: Int,
    val login_pin: String,
    val password_hash: String,
    val phone: String,
    val shop_name: String,
    val updated_at: String
)