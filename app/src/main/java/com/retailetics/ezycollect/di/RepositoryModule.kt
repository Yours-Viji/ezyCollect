package com.retailetics.ezycollect.di

import com.retailetics.ezycollect.data.datastore.PreferencesManager
import com.retailetics.ezycollect.data.remote.api.AuthApi
import com.retailetics.ezycollect.data.remote.interceptors.AuthInterceptor
import com.retailetics.ezycollect.data.repository.AuthRepositoryImpl
import com.retailetics.ezycollect.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        preferencesManager: PreferencesManager
    ): AuthRepository {
        return AuthRepositoryImpl(authApi, preferencesManager)
    }
}