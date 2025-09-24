package com.retailetics.ezycollect.presentation.login

data class LoginState(
    val pin: String = "123456",
    val email: String = "baskar@retailetics.com",
    val password: String = "123456",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)