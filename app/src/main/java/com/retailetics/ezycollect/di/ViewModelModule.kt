package com.retailetics.ezycollect.di

import com.retailetics.ezycollect.domain.repository.AuthRepository
import com.retailetics.ezycollect.domain.usecase.GetAuthDataUseCase
import com.retailetics.ezycollect.domain.usecase.LoginUseCase
import com.retailetics.ezycollect.domain.usecase.SaveAuthTokenUseCase
import com.retailetics.ezycollect.domain.usecase.ShoppingUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideShoppingUseCase(authRepository: AuthRepository): ShoppingUseCase {
        return ShoppingUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetAuthDataUseCase(authRepository: AuthRepository): GetAuthDataUseCase {
        return GetAuthDataUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideSaveAuthTokenUseCase(authRepository: AuthRepository): SaveAuthTokenUseCase {
        return SaveAuthTokenUseCase(authRepository)
    }
}