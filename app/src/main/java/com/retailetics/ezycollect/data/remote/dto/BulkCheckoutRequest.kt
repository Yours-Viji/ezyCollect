package com.retailetics.ezycollect.data.remote.dto

data class BulkCheckoutRequest(
    val items: List<BulkCheckoutItem>
)

data class BulkCheckoutItem(
    val product_name: String,
    val price: Double,
    val quantity: Int
)

/*
// Response class
data class CheckoutResponse(
    val success: Boolean,
    val transactionId: String,
    val message: String
)*/
