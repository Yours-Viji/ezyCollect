package com.retailetics.ezycollect.data.remote.dto


data class ShoppingCartDetails(
    val items: List<Item>,
    val subtotal: Int
)

data class Item(
    val id: Int,
    val price: Int,
    val product_name: String,
    val quantity: Int
)
