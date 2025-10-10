package com.retailetics.ezycollect.domain.repository

import com.retailetics.ezycollect.data.remote.dto.BulkCheckoutRequest
import com.retailetics.ezycollect.data.remote.dto.CheckoutResponse
import com.retailetics.ezycollect.data.remote.dto.CreateCartResponse
import com.retailetics.ezycollect.data.remote.dto.LoginResponse
import com.retailetics.ezycollect.data.remote.dto.NetworkResponse
import com.retailetics.ezycollect.data.remote.dto.RegistrationResult
import com.retailetics.ezycollect.data.remote.dto.ShoppingCartDetails
import com.retailetics.ezycollect.data.remote.dto.TransactionReportData

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String,password: String,pin: String): NetworkResponse<LoginResponse>
    suspend fun registration(fullName: String, shopName: String, phone: String, email: String, address: String, bankAccount: String,bankName: String, password: String, loginPin: String, biometricEnabled: Boolean, termsAccepted: Boolean): NetworkResponse<RegistrationResult>

    suspend fun createNewShoppingCart(): NetworkResponse<CreateCartResponse>
    suspend fun getShoppingCartDetails(): NetworkResponse<ShoppingCartDetails>
    suspend fun addProductToShoppingCart(name: String,quantity:Int,price:Double): NetworkResponse<ShoppingCartDetails>
    suspend fun addBulkItemsToShoppingCart(bulkCheckoutRequest: BulkCheckoutRequest): NetworkResponse<ShoppingCartDetails>
    suspend fun deleteProductFromShoppingCart(id:Int): NetworkResponse<ShoppingCartDetails>
    suspend fun editProductInCart(id:Int,price:Double,quantity:Int): NetworkResponse<ShoppingCartDetails>
    suspend fun checkout(merchantId: String,paymentMethod: String): NetworkResponse<CheckoutResponse>
    suspend fun getTransactionReport(startDate: String,endDate:String): NetworkResponse<TransactionReportData>
    suspend fun saveAuthToken(token: String)
    suspend fun getAuthToken(): String?
    fun isDeviceActivated(): Flow<Boolean?>
    fun getCartId(): Flow<String>
}