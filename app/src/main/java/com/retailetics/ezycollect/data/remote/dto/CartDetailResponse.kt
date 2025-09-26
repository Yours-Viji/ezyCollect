package com.retailetics.ezycollect.data.remote.dto


data class ShoppingCartDetails(
    val items: List<Item>,
    val subtotal: Double
)

data class Item(
    val id: Int,
    val price: Double,
    val product_name: String,
    val quantity: Int
)
