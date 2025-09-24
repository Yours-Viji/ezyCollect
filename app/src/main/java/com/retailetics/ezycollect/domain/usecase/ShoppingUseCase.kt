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

    suspend  fun getProductDetails(barCode: String): NetworkResponse<ProductInfo> {
        return authRepository.getProductDetails(barCode)
    }

    suspend  fun getPriceDetails(barCode: String): NetworkResponse<ProductPriceInfo> {
        return authRepository.getPriceDetails(barCode)
    }

    suspend  fun getShoppingCartDetails(): NetworkResponse<ShoppingCartDetails> {
        return authRepository.getShoppingCartDetails()
    }

    suspend  fun getPaymentSummary(): NetworkResponse<ShoppingCartDetails> {
        return authRepository.getPaymentSummary()
    }

    /*suspend  fun addToCart(barCode: String,quantity:Int): NetworkResponse<ShoppingCartDetails> {
        return authRepository.addProductToShoppingCart(barCode,quantity)
    }
*/
    suspend  fun editProductInCart(barCode: String,quantity:Int,id:Int): NetworkResponse<ShoppingCartDetails> {
        return authRepository.editProductInCart(barCode,id,quantity)
    }

    suspend  fun deleteProductFromCart(barCode: String,id:Int): NetworkResponse<ShoppingCartDetails> {
        return authRepository.deleteProductFromShoppingCart(barCode,id)
    }


}