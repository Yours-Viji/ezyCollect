package com.retailetics.ezycollect.presentation.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.retailetics.ezycollect.data.datastore.PreferencesManager
import com.retailetics.ezycollect.data.remote.dto.Item
import com.retailetics.ezycollect.data.remote.dto.NetworkResponse

import com.retailetics.ezycollect.data.remote.dto.ShoppingCartDetails
import com.retailetics.ezycollect.domain.model.AppMode
import com.retailetics.ezycollect.domain.usecase.GetCartIdUseCase
import com.retailetics.ezycollect.domain.usecase.LoadingManager
import com.retailetics.ezycollect.domain.usecase.ShoppingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val shoppingUseCase: ShoppingUseCase,
    private val getCartIdUseCase: GetCartIdUseCase,
    private val preferencesManager: PreferencesManager,
    private val loadingManager: LoadingManager
) : ViewModel() {
    private val _stateFlow = MutableStateFlow(HomeState())
    val stateFlow: StateFlow<HomeState> = _stateFlow.asStateFlow()

    private val _cartDataList = MutableStateFlow<List<Item>>(emptyList())
    val cartDataList: StateFlow<List<Item>> = _cartDataList.asStateFlow()

    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    private val _totalAmount = MutableStateFlow(0.0)
    val totalAmount: StateFlow<Double> = _totalAmount.asStateFlow()



    fun initNewShopping(){
        clearCartDetails()
        createNewShoppingCart()
    }
    private fun clearCartDetails() {
        _stateFlow.value = HomeState()
        _cartDataList.value = emptyList()
        _cartCount.value = 0
        _totalAmount.value = 0.0
    }
    fun setLoggedOut(){
        viewModelScope.launch {
            preferencesManager.setLoggedIn(false)
            _stateFlow.value = _stateFlow.value.copy(
                isLoading = false,
                error = "LogOut Success",
                isLogOutSuccess = true
            )
        }
    }
    private fun createNewShoppingCart() {
        loadingManager.show()
        clearCartDetails()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)

            when (val result = shoppingUseCase.createNewShoppingCart()) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                    )
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message ?: "Unable to create cart",
                    )
                    loadingManager.hide()
                }
            }
        }
    }



    fun addProductToShoppingCart(name: String,quantity:Int,price:Double) {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            when (val result = shoppingUseCase.addToCart(name,quantity,price)) {

                is NetworkResponse.Success -> {
                    loadingManager.hide()
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _cartDataList.value=result.data.items
                    _cartCount.value = result.data.items.size
                    _totalAmount.value=result.data.subtotal


                }
                is NetworkResponse.Error -> {
                    loadingManager.hide()
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                }
            }
        }
    }

    fun editProductInShoppingCart(price: Double,quantity:Int,id:Int) {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            when (val result = shoppingUseCase.editProductInCart(price,quantity,id)) {
                is NetworkResponse.Success -> {
                    loadingManager.hide()
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _cartDataList.value=result.data.items
                    _cartCount.value = result.data.items.size
                    _totalAmount.value=result.data.subtotal.toDouble()

                }
                is NetworkResponse.Error -> {
                    loadingManager.hide()
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )

                }
            }
        }
    }


    fun deleteProductFromShoppingCart(id:Int) {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            when (val result = shoppingUseCase.deleteProductFromCart(id)) {
                is NetworkResponse.Success -> {
                    loadingManager.hide()
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _cartDataList.value=result.data.items
                    _cartCount.value = result.data.items.size
                    _totalAmount.value=result.data.subtotal.toDouble()

                }
                is NetworkResponse.Error -> {
                    loadingManager.hide()
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )

                }
            }
        }
    }

     fun checkout(paymentMethod:String) {
        loadingManager.show()
        clearCartDetails()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)

            when (val result = shoppingUseCase.checkout(preferencesManager.getMerchantId(),paymentMethod)) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                    )
                    initNewShopping()
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message ?: "Unable to store transaction",
                    )
                    loadingManager.hide()
                }
            }
        }
    }


}