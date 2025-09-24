package com.retailetics.ezycollect.data.remote.dto

data class EditProductRequest (
    val id:Int,
    val barcode:String,
    val quantity:Int,
)