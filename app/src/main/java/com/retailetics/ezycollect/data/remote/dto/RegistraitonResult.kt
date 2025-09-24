package com.retailetics.ezycollect.data.remote.dto

data class RegistrationResult(
    val merchantId: MerchantId,
    val message: String
)
data class MerchantId(
    val merchant: MerchantDetails,
    val token: String
)

data class MerchantDetails(
    val email: String,
    val id: Int,
    val name: String
)