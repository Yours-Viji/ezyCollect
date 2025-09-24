package com.retailetics.ezycollect.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String
)