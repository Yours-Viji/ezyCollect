package com.retailetics.ezycollect.data.remote.dto

data class TransactionReportData(
    val `data`: List<Data>
)
data class Data(
    val created_at: String,
    val full_name: String,
    val items: List<TransactionData>,
    val order_id: Int,
    val shop_name: String,
    val total_amount: Int
)
data class TransactionData(
    val line_total: Int,
    val product_name: String,
    val qty: Int,
    val unit_price: Int
)