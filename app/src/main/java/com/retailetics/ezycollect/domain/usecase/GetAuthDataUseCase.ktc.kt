package com.retailetics.ezycollect.domain.usecase

import com.retailetics.ezycollect.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthDataUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.isDeviceActivated()
    }

}