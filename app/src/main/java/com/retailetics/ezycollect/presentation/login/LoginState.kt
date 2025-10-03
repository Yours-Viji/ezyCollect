package com.retailetics.ezycollect.presentation.login

data class LoginState(
    val pin: String = "20725",
    val email: String = "arivu100@gmail.com",
    val password: String = "arivu@123",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false


   /* val pin: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false*/
)