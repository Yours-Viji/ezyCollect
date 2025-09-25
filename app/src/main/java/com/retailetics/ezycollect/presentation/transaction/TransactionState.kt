package com.retailetics.ezycollect.presentation.transaction

data class TransactionState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPaymentSuccess: Boolean = false
)