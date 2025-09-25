package com.retailetics.ezycollect.domain.usecase

import com.retailetics.ezycollect.data.remote.dto.LoginResponse
import com.retailetics.ezycollect.data.remote.dto.NetworkResponse
import com.retailetics.ezycollect.data.remote.dto.RegistrationResult
import com.retailetics.ezycollect.domain.repository.AuthRepository

import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String,password: String,pin: String): NetworkResponse<LoginResponse> {
        return authRepository.login(email,password,pin)
    }

    suspend  fun registration(fullName: String, shopName: String, phone: String, email: String, address: String, bankAccount: String,bankName: String, password: String, loginPin: String, biometricEnabled: Boolean, termsAccepted: Boolean): NetworkResponse<RegistrationResult> {
        return authRepository.registration(fullName, shopName,phone,email,address,bankAccount,bankName,password,loginPin,biometricEnabled,termsAccepted)
    }

}