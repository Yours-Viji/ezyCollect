package com.retailetics.ezycollect.data.remote.dto

data class CheckoutRequest(
    val merchantId: Int,
    val paymentMethod: String
)