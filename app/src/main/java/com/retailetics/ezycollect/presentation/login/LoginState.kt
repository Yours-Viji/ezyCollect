package com.retailetics.ezycollect.presentation.login

data class LoginState(
    val pin: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)