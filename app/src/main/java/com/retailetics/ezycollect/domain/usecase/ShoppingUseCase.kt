package com.retailetics.ezycollect.domain.usecase

import com.retailetics.ezycollect.data.remote.dto.CreateCartResponse
import com.retailetics.ezycollect.data.remote.dto.NetworkResponse
import com.retailetics.ezycollect.data.remote.dto.ShoppingCartDetails
import com.retailetics.ezycollect.domain.repository.AuthRepository

import com.retailetics.ezycollect.model.ProductInfo
import com.retailetics.ezycollect.model.ProductPriceInfo
import javax.inject.Inject

class ShoppingUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): NetworkResponse<ShoppingCartDetails> {
        return authRepository.getShoppingCartDetails()
    }

    suspend  fun createNewShoppingCart(): NetworkResponse<CreateCartResponse> {
        return authRepository.createNewShoppingCart()
    }

    suspend  fun addToCart(name: String,quantity:Int,price:Double): NetworkResponse<ShoppingCartDetails> {
        return authRepository.addProductToShoppingCart(name,quantity,price)
    }

    suspend  fun editProductInCart(price: Double,quantity:Int,id:Int): NetworkResponse<ShoppingCartDetails> {
        return authRepository.editProductInCart(id,price,quantity)
    }

    suspend  fun deleteProductFromCart(id:Int): NetworkResponse<ShoppingCartDetails> {
        return authRepository.deleteProductFromShoppingCart(id)
    }




}