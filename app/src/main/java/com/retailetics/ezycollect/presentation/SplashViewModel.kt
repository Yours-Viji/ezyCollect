package com.retailetics.ezycollect.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retailetics.ezycollect.data.datastore.PreferencesManager
import com.retailetics.ezycollect.domain.usecase.GetAuthDataUseCase
import com.retailetics.ezycollect.presentation.common.data.Constants
import com.retailetics.ezycollect.presentation.common.data.DeviceUtils
import com.retailetics.ezycollect.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// In SplashViewModel
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getAuthDataUseCase: GetAuthDataUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null) // Start with null
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    val isDeviceActivated: StateFlow<Boolean?> = getAuthDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Combined start destination state
    val startDestination: StateFlow<String?> = combine(
        isDeviceActivated,
        isLoggedIn
    ) { isActivated, isLoggedIn ->
        when {
            isActivated == null || isLoggedIn == null -> "activation" // Still loading
            !isActivated -> "activation" // Not activated
            isActivated && isLoggedIn -> "home" // Activated and logged in
            else -> "login" // Activated but not logged in
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        checkIsLoggedIn()
    }

    private fun checkIsLoggedIn() {
        viewModelScope.launch {
            _isLoggedIn.value = preferencesManager.isLoggedIn()
        }
    }

    fun getDeviceId(mainActivity: MainActivity) {
        val deviceId = DeviceUtils.getDeviceId(mainActivity)
        Constants.deviceId = deviceId
    }

    fun clearUserPreference(){
        viewModelScope.launch(Dispatchers.IO) {
            preferencesManager.clearEmployeeDetails()
        }
    }
}