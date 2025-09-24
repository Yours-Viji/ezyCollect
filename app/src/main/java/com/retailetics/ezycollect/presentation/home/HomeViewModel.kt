package com.retailetics.ezycollect.presentation.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.retailetics.ezycollect.data.datastore.PreferencesManager
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

   /* private val _cartDataList = MutableStateFlow<List<Item>>(emptyList())
    val cartDataList: StateFlow<List<Item>> = _cartDataList.asStateFlow()*/

    private val _cartCount = MutableStateFlow(0)
    val cartCount: StateFlow<Int> = _cartCount.asStateFlow()

    private val _shoppingCartInfo = MutableStateFlow<ShoppingCartDetails?>(null)
    val shoppingCartInfo: StateFlow<ShoppingCartDetails?> = _shoppingCartInfo.asStateFlow()



    private val _isPickerModel = MutableStateFlow<Boolean>(false)
    val isPickerModel: StateFlow<Boolean> = _isPickerModel.asStateFlow()

    private val _appMode = MutableStateFlow(AppMode.EzyCartPicker)
    val appMode: StateFlow<AppMode> = _appMode

    private val _employeeName = MutableStateFlow("")
    val employeeName: StateFlow<String> = _employeeName.asStateFlow()

    val cartId: StateFlow<String?> = getCartIdUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    init {
        viewModelScope.launch {
            val savedAppMode = preferencesManager.getAppMode()
            _appMode.update { savedAppMode }
            _isPickerModel.update { savedAppMode.name == AppMode.EzyCartPicker.name }
            _employeeName.update { preferencesManager.getEmployeeName() }

        }

    }

    fun onAppModeChange(selectedAppMode:AppMode) {
        viewModelScope.launch {
            _isPickerModel.update {selectedAppMode.name == AppMode.EzyCartPicker.name}
            preferencesManager.setAppMode(selectedAppMode)
        }
    }
    fun initNewShopping(){
        clearCartDetails()
        //createNewShoppingCart()
    }
   private fun clearCartDetails(){

    }
   /* private fun createNewShoppingCart() {
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

    fun getShoppingCartDetails() {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            when (val result = shoppingUseCase()) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _cartDataList.value=result.data.items
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    loadingManager.hide()
                }
            }
        }
    }*/

    private fun getPaymentSummary() {
       /* loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)

            when (val result = shoppingUseCase.getPaymentSummary()) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _shoppingCartInfo.value=result.data
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    loadingManager.hide()
                }
            }
        }*/
    }

    /*fun addProductToShoppingCart(name: String,quantity:Int,price:Double) {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            when (val result = shoppingUseCase.addToCart(name,quantity,price)) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _cartDataList.value=result.data.items
                    _cartCount.value = result.data.items.size
                    loadingManager.hide()
                    getPaymentSummary()

                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    loadingManager.hide()
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
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _cartDataList.value=result.data.items
                    _cartCount.value = result.data.items.size
                    loadingManager.hide()
                    getPaymentSummary()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    loadingManager.hide()
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
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _cartDataList.value=result.data.items
                    _cartCount.value = result.data.items.size
                    loadingManager.hide()
                    getPaymentSummary()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    loadingManager.hide()
                }
            }
        }
    }*/

   /* fun getProductDetails(barCode:String) {
        loadingManager.show()
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            _productInfo.value = null
            when (val result = shoppingUseCase.getProductDetails(barCode)) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _productInfo.value=result.data
                    getPriceDetails(barCode)
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    loadingManager.hide()
                    //End of input at line 1 column 1 path
                }
            }
        }
    }*/
    fun resetProductInfoDetails() {

    }

   /* private fun getPriceDetails(barCode:String) {
        viewModelScope.launch {
            _stateFlow.value = _stateFlow.value.copy(isLoading = true, error = null)
            _priceDetails.value = null
            when (val result = shoppingUseCase.getPriceDetails(barCode)) {
                is NetworkResponse.Success -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false
                    )
                    _priceDetails.value=result.data
                    loadingManager.hide()
                }
                is NetworkResponse.Error -> {
                    _stateFlow.value = _stateFlow.value.copy(
                        isLoading = false,
                        error = result.message,
                    )
                    loadingManager.hide()
                }
            }
        }
    }*/


}