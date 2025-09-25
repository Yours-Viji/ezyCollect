package com.retailetics.ezycollect.data.remote.api

import com.retailetics.ezycollect.data.remote.dto.AddProductToCartRequest
import com.retailetics.ezycollect.data.remote.dto.ApiResponse
import com.retailetics.ezycollect.data.remote.dto.CheckoutRequest
import com.retailetics.ezycollect.data.remote.dto.CheckoutResponse
import com.retailetics.ezycollect.data.remote.dto.CreateCartResponse

import com.retailetics.ezycollect.data.remote.dto.EditProductRequest
import com.retailetics.ezycollect.data.remote.dto.LoginRequest
import com.retailetics.ezycollect.data.remote.dto.LoginResponse
import com.retailetics.ezycollect.data.remote.dto.RegistrationRequest
import com.retailetics.ezycollect.data.remote.dto.RegistrationResult
import com.retailetics.ezycollect.data.remote.dto.ShoppingCartDetails
import com.retailetics.ezycollect.data.remote.dto.TransactionReportData

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP

import retrofit2.http.PATCH
import retrofit2.http.POST

import retrofit2.http.Path


interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/auth/register")
    suspend fun registration(@Body registrationRequest: RegistrationRequest): Response<ApiResponse<RegistrationResult>>

    @POST("/cart")
    suspend fun createShoppingCart(): Response<CreateCartResponse>


    @POST("/cart/items")
    suspend fun addProductToCartApi(@Body dddProductToCartRequest: AddProductToCartRequest): Response<ShoppingCartDetails>

    @HTTP(method = "DELETE", path = "/cart/{cart_Id}/items/{id}", hasBody = true)
    suspend fun deleteProductFromCart(
        @Path(value = "cart_Id", encoded = true) cartId: String,
        @Path(value = "id", encoded = true) productId: Int,
    ): Response<ShoppingCartDetails>

    @PATCH("/cart/{cart_Id}/items/{id}")
    suspend fun editProductQuantity(
        @Path(value = "cart_Id", encoded = true) cartId: String,
        @Path(value = "id", encoded = true) productId: Int,
        @Body editProductRequest: EditProductRequest
    ): Response<ShoppingCartDetails>



    @GET("/cart")
    suspend fun getCartShoppingDetails(): Response<ShoppingCartDetails>

    @POST("/cart/checkout")
    suspend fun checkoutApi(@Body checkoutRequest: CheckoutRequest): Response<CheckoutResponse>

    @GET("/report/transaction")
    suspend fun getTransactionReport(): Response<TransactionReportData>
}