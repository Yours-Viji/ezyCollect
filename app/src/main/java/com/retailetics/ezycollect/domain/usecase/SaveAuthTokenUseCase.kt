package com.retailetics.ezycollect.domain.usecase

import com.retailetics.ezycollect.domain.repository.AuthRepository

import javax.inject.Inject

class SaveAuthTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(token: String) {
        authRepository.saveAuthToken(token)
    }
}