package com.retailetics.ezycollect.data.remote.dto


data class LoginRequest(
   val email: String,
   val password: String,
   val pin: String
)