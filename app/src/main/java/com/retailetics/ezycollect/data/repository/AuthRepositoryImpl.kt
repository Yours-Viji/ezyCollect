package com.retailetics.ezycollect.data.repository

import com.retailetics.ezycollect.data.datastore.PreferencesManager
import com.retailetics.ezycollect.data.remote.api.AuthApi
import com.retailetics.ezycollect.data.remote.dto.*
import com.retailetics.ezycollect.domain.repository.AuthRepository

import com.retailetics.ezycollect.presentation.common.data.Constants
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override suspend fun login(email: String,password: String,pin: String): NetworkResponse<LoginResponse> {
        return safeApiCallRaw { authApi.login(LoginRequest(
            email,
            password,
            pin
        )) }
            .also { result ->
                if (result is NetworkResponse.Success) {
                    preferencesManager.saveAuthToken(result.data.token)
                    preferencesManager.saveEmployeeDetails(result.data)

                }
            }
    }

    override suspend fun registration(
        fullName: String, shopName: String, phone: String, email: String, address: String, bankAccount: String,bankName: String, password: String, loginPin: String, biometricEnabled: Boolean, termsAccepted: Boolean
    ): NetworkResponse<RegistrationResult> {
        return safeApiCall {
            authApi.registration(RegistrationRequest(address = address, bankAccount = bankAccount, bankName = bankName,biometricEnabled = biometricEnabled, termsAccepted = termsAccepted,
                fullName = fullName, shopName = shopName, phone = phone, email = email, loginPin = loginPin, password = password))
        }.also { result ->
            if (result is NetworkResponse.Success) {
                preferencesManager.setDeviceActivated()
            }
        }
    }



    override suspend fun createNewShoppingCart(): NetworkResponse<CreateCartResponse> {
        return safeApiCallRaw { authApi.createShoppingCart() }
            .also { result ->
                if (result is NetworkResponse.Success) {
                    preferencesManager.saveCartId("${result.data.id}")
                }
            }
    }


    override suspend fun getShoppingCartDetails(): NetworkResponse<ShoppingCartDetails> {
        return safeApiCallRaw { authApi.getCartShoppingDetails(preferencesManager.getShoppingCartId()) }
            .also { result ->
                if (result is NetworkResponse.Success) {
                }
            }
    }

    override suspend fun addProductToShoppingCart(name: String,quantity:Int,price:Double): NetworkResponse<ShoppingCartDetails> {
        return safeApiCallRaw { authApi.addProductToCartApi(AddProductToCartRequest(name,quantity,price)) }
            .also { result ->
                if (result is NetworkResponse.Success) {
                }
            }
    }

    override suspend fun editProductInCart(id:Int,price: Double,quantity:Int): NetworkResponse<ShoppingCartDetails> {
        return safeApiCallRaw { authApi.editProductQuantity(preferencesManager.getShoppingCartId(),id,EditProductRequest(price,quantity)) }
            .also { result ->
                if (result is NetworkResponse.Success) {
                }
            }
    }

    override suspend fun deleteProductFromShoppingCart(id:Int): NetworkResponse<ShoppingCartDetails> {
        return safeApiCallRaw { authApi.deleteProductFromCart(preferencesManager.getShoppingCartId(),id) }
            .also { result ->
                if (result is NetworkResponse.Success) {
                }
            }
    }

    private suspend fun getMerchantParam(): HashMap<String, String> {
        val params = HashMap<String, String>()
        params["merchantId"] = "" + preferencesManager.getMerchantId()
        params["outletId"] = "" + preferencesManager.getOutletId()
        params["isMemberLogin"] = "false"

        return params
    }


        private suspend fun getCreateCartRequestData(): CreateCartRequest {
            val prefs = preferencesManager.userPreferencesFlow.first()
            val outletId = preferencesManager.getOutletId()
            val merchantId = preferencesManager.getMerchantId()
            val appMode = preferencesManager.getAppMode()

            return CreateCartRequest(
                employeeId = prefs.employeeId.toString(),
                memberNumber = "",
                userId = "",
                name = prefs.employeeName,
                deviceId = Constants.deviceId,
                outletId = outletId,
                merchantId = merchantId,
                appMode = appMode.name,
                trolleyNo = "01"
            )
        }

    override suspend fun saveAuthToken(token: String) {
        preferencesManager.saveAuthToken(token)
    }

    override suspend fun getAuthToken(): String? {
        return preferencesManager.getAuthToken()
    }

    override fun isDeviceActivated(): Flow<Boolean> {
        return preferencesManager.isDeviceActivated()
    }

    override fun getCartId(): Flow<String> {
        return preferencesManager.getCartId()
    }

   /* private fun LoginResponse.toUser(): User {
        return User(
            id = id,
            email = email,
            name = name,
            token = token
        )
    }*/

    /**
     * For APIs that return ApiResponse<T>.
     */
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): NetworkResponse<T> {
        return try {
            val response = apiCall()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                when {
                    body.data != null -> NetworkResponse.Success(body.data)
                    body.error != null -> NetworkResponse.Error(
                        body.error.message ?: "Unknown API error",
                        body.error.code
                    )
                    body.message != null -> NetworkResponse.Error(body.message, response.code())
                    else -> NetworkResponse.Error("Empty response body", response.code())
                }
            } else {
                val rawJson = response.errorBody()?.string()
                NetworkResponse.Error(parseErrorMessage(rawJson), response.code())
            }
        } catch (e: Exception) {
            NetworkResponse.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

    /**
     * For APIs that return raw model (not wrapped).
     */
    private suspend fun <T> safeApiCallRaw(
        apiCall: suspend () -> Response<T>
    ): NetworkResponse<T> {
        return try {
            val response = apiCall()
            val body = response.body()

            if (response.isSuccessful && body != null) {
                NetworkResponse.Success(body)
            } else {
                val rawJson = response.errorBody()?.string()
                NetworkResponse.Error(parseErrorMessage(rawJson), response.code())
            }
        } catch (e: Exception) {
            NetworkResponse.Error(e.localizedMessage ?: "Unexpected error")
        }
    }

    /**
     * Tries to parse error JSON into a human-readable message.
     */
    private fun parseErrorMessage(rawJson: String?): String {
        if (rawJson.isNullOrBlank()) return "Unknown error"

        return try {
            // Try parsing as ApiResponse
            val apiResponse = Gson().fromJson(rawJson, ApiResponse::class.java)
            apiResponse?.error?.message
                ?: apiResponse?.message
                ?: "Unknown API error"
        } catch (_: Exception) {
            try {
                // Try parsing as GenericErrorResponse
                val genericError = Gson().fromJson(rawJson, GenericErrorResponse::class.java)
                genericError?.message
                    ?: genericError?.publicMessage
                    ?: genericError?.error
                    ?: "Unknown server error"
            } catch (_: Exception) {
                rawJson // fallback: return raw JSON
            }
        }
    }
}
