package com.retailetics.ezycollect.data.remote.dto

data class CreateCartResponse(
    val created_at: String,
    val id: Int,
    val merchant_id: Int,
    val status: String,
    val updated_at: String
)
