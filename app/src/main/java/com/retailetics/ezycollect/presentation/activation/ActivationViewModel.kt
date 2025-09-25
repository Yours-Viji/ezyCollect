package com.retailetics.ezycollect.presentation.activation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retailetics.ezycollect.data.datastore.PreferencesManager
import com.retailetics.ezycollect.data.remote.dto.NetworkResponse
import com.retailetics.ezycollect.domain.model.AppMode
import com.retailetics.ezycollect.domain.usecase.GetAuthDataUseCase
import com.retailetics.ezycollect.domain.usecase.LoadingManager
import com.retailetics.ezycollect.domain.usecase.LoginUseCase
import com.retailetics.ezycollect.presentation.common.data.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getAuthDataUseCase: GetAuthDataUseCase,
    private val preferencesManager: PreferencesManager,
    private val loadingManager: LoadingManager
) : ViewModel() {

    private val _stateFlow = MutableStateFlow(ActivationState())
    val stateFlow: StateFlow<ActivationState> = _stateFlow.asStateFlow()
    val isDeviceActivated: StateFlow<Boolean?> = getAuthDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /*init {
        viewModelScope.launch {
            val savedAppMode = preferencesManager.getAppMode()
            _stateFlow.update {
                it.copy(
                    activationCode = "ALpxvmI0111",
                    trolleyNumber = "01",
                )
            }
        }
    }*/


    val authState = getAuthDataUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    fun onBiometricEnabledChange(isEnabled: Boolean) {
        _stateFlow.value = _stateFlow.value.copy(biometricEnabled = isEnabled)
    }

    fun onPinChange(loginPin: String) {
        _stateFlow.value = _stateFlow.value.copy(loginPin = loginPin)
    }
    fun onShopNameChange(shopName: String) {
        _stateFlow.value = _stateFlow.value.copy(shopName = shopName)
    }
    fun onTermsAcceptedChange(termsAccepted: Boolean) {
        _stateFlow.value = _stateFlow.value.copy(termsAccepted = termsAccepted)
    }
    fun onNameChange(name: String) {
        _stateFlow.value = _stateFlow.value.copy(fullName = name)
    }

    fun onEmailChange(email: String) {
        _stateFlow.value = _stateFlow.value.copy(email = email)
    }
    fun onAddressChange(address: String) {
        _stateFlow.value = _stateFlow.value.copy(address = address)
    }
    fun onBankAccountChange(account: String) {
        _stateFlow.value = _stateFlow.value.copy(bankAccount = account)
    }
    fun onBankNameChange(bankName: String) {
        _stateFlow.value = _stateFlow.value.copy(bankName = bankName)
    }
    fun onPhoneChange(phone: String) {
        _stateFlow.value = _stateFlow.value.copy(phone = phone)
    }
    fun onPasswordChange(password: String) {
        _stateFlow.value = _stateFlow.value.copy(password = password)
    }
    fun onErrorShown() {
        _stateFlow.value = _stateFlow.value.copy(error = null)
    }
    fun activateDeviceForTesting(){
        CoroutineScope(Dispatchers.IO).launch {
            preferencesManager.setDeviceActivated()
        }
    }
    fun activateDevice() {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)

            when (val result = loginUseCase.registration(
                _stateFlow.value.fullName,
                _stateFlow.value.shopName,
                _stateFlow.value.phone,
                _stateFlow.value.email,
                _stateFlow.value.address,
                _stateFlow.value.bankAccount,
                _stateFlow.value.bankName,
                _stateFlow.value.password,
                _stateFlow.value.loginPin,
                _stateFlow.value.biometricEnabled,
                _stateFlow.value.termsAccepted
            )) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        isActivationSuccessful = true
                    )
                    //saveActivationDetails("${result.data.id}", "${result.data.merchantId}")
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message ?: "Activation failed",
                        isActivationSuccessful = false
                    )
                    if(result.message.contains("Error: Already a device activated with same device Id")){
                       // getDeviceInfo()
                    }
                    loadingManager.hide()
                }
            }
        }
    }

    /*fun getDeviceInfo() {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)

            when (val result = loginUseCase.deviceDetails(Constants.deviceId)) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        isActivationSuccessful = true
                    )
                    saveActivationDetails("${result.data.outletId}", "${result.data.merchantId}")
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message ?: "Device info fetch failed",
                        isActivationSuccessful = false
                    )
                    loadingManager.hide()
                }
            }
        }
    }*/



    private suspend  fun saveActivationDetails(outletId:String,merchantId:String) {
        preferencesManager.setDeviceActivated()
        preferencesManager.saveOutletId(outletId)
        preferencesManager.saveMerchantId(merchantId)
    }
}
