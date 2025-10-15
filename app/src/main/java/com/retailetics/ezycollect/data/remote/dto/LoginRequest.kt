package com.retailetics.ezycollect.data.remote.dto


data class LoginRequest(
   val userName: String,
   val password: String,
   val pin: String
)