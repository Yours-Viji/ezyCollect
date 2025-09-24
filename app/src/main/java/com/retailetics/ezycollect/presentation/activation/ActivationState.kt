package com.retailetics.ezycollect.presentation.activation

import com.retailetics.ezycollect.domain.model.AppMode

data class ActivationState(

    val address: String= "",
    val bankAccount: String= "",
    val bankName: String= "",
    val biometricEnabled: Boolean= false,
    val email: String= "",
    val fullName: String= "",
    val loginPin: String= "",
    val password: String= "",
    val phone: String= "",
    val shopName: String= "",
    val termsAccepted: Boolean= false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isActivationSuccessful: Boolean = false
)