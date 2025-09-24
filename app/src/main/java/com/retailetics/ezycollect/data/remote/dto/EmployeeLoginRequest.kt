package com.retailetics.ezycollect.model


data class EmployeeLoginRequest(
    val employeePin:String,
    val merchantId:String,
    val outletId:String
   // var registrationToken=""
)
