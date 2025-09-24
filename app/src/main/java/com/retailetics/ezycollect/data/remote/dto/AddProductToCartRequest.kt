package com.retailetics.ezycollect.data.remote.dto

data class AddProductToCartRequest(
    val product_name:String,
    val quantity:Int,
    val price:Double,
)
