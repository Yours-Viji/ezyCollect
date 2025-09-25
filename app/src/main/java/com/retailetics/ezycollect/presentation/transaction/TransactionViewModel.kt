package com.retailetics.ezycollect.presentation.transaction


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.retailetics.ezycollect.data.datastore.PreferencesManager
import com.retailetics.ezycollect.data.remote.dto.NetworkResponse
import com.retailetics.ezycollect.data.remote.dto.TransactionReport


import com.retailetics.ezycollect.domain.usecase.GetCartIdUseCase
import com.retailetics.ezycollect.domain.usecase.LoadingManager
import com.retailetics.ezycollect.domain.usecase.ShoppingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val shoppingUseCase: ShoppingUseCase,
    private val getCartIdUseCase: GetCartIdUseCase,
    private val preferencesManager: PreferencesManager,
    private val loadingManager: LoadingManager
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(TransactionState())
    val stateFlow: StateFlow<TransactionState> = _stateFlow.asStateFlow()

    private val _transactionReport = MutableStateFlow<List<TransactionReport>>(emptyList())
    val transactionReport: StateFlow<List<TransactionReport>> = _transactionReport.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> get() = _error

    private val _totalCollection = MutableStateFlow(0)
    val totalCollection: StateFlow<Int> get() = _totalCollection

    private val _totalTransaction = MutableStateFlow(0)
    val totalTransaction: StateFlow<Int> get() = _totalTransaction


    fun getShoppingCartDetails(startDate: String, endDate: String) {
       loadingManager.show()
       _isLoading.value = true
       _error.value = ""
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            when (val result = shoppingUseCase.getTransactionReport(startDate,endDate)) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _transactionReport.value = result.data.transactions
                    _totalCollection.value=result.data.totalCollection
                    _totalTransaction.value=result.data.count
                    loadingManager.hide()
                    _isLoading.value = false

                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    _error.value = "Failed to load transactions: ${result.message}"
                    _transactionReport.value = emptyList()
                    loadingManager.hide()
                    _isLoading.value = false
                }
            }
        }
    }
    fun clearTransactions() {
        _transactionReport.value = emptyList()
        _error.value = ""
    }
}