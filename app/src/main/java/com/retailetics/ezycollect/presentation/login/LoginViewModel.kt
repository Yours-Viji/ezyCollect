package com.retailetics.ezycollect.presentation.login

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retailetics.ezycollect.data.datastore.PreferencesManager
import com.retailetics.ezycollect.data.datastore.model.UserPreferences
import com.retailetics.ezycollect.data.remote.dto.NetworkResponse
import com.retailetics.ezycollect.domain.usecase.GetAuthDataUseCase
import com.retailetics.ezycollect.domain.usecase.LoadingManager
import com.retailetics.ezycollect.domain.usecase.LoginUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getAuthDataUseCase: GetAuthDataUseCase,
    private val preferencesManager: PreferencesManager,
    private val loadingManager: LoadingManager
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(LoginState())
    val stateFlow: StateFlow<LoginState> = _stateFlow.asStateFlow()
    val userPreferences: StateFlow<UserPreferences> =
        preferencesManager.userPreferencesFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UserPreferences()
            )
    val authState = getAuthDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun onPinChange(pin: String) {
        _stateFlow.value = _stateFlow.value.copy(pin = pin)
    }
    fun onPasswordChange(password: String) {
        _stateFlow.value = _stateFlow.value.copy(password = password)
    }
    fun onEmailChange(email: String) {
        _stateFlow.value = _stateFlow.value.copy(email = email)
    }

    fun testLogin(){
        _stateFlow.value = _stateFlow.value.copy(
            isLoading = false,
            isLoginSuccessful = true
        )
    }
    fun login(pin: String = _stateFlow.value.pin,email: String=_stateFlow.value.email,password:String=_stateFlow.value.password) {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)

            when (val result = loginUseCase(email,password,"")) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                        isLoginSuccessful = false
                    )
                    loadingManager.hide()
                }
            }
        }
    }


}