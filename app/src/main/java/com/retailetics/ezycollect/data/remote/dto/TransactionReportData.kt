package com.retailetics.ezycollect.data.remote.dto

data class TransactionReportData(
    val count: Int,
    val totalCollection: Int,
    val transactions: List<TransactionReport>
)
data class TransactionReport(
    val created_at: String,
    val full_name: String,
    val items: List<TransactionData>,
    val order_id: Int,
    val shop_name: String,
    val total_amount: String
)
data class TransactionData(
    val line_total: Int,
    val product_name: String,
    val qty: Int,
    val unit_price: Int
)

