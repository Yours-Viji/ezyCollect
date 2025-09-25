package com.retailetics.ezycollect.data.remote.dto

data class CheckoutResponse(
    val items: List<CheckoutData>,
    val orderId: Int,
    val paymentMethod: String,
    val total: Int
)
data class CheckoutData(
    val line_total: Int,
    val product_name: String,
    val qty: Int,
    val unit_price: Int
)