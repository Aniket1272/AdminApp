package com.example.admincontroll.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val companyName: String,
    val addressOfCompany: String,
    val emailOfCompany: String,
    val password: String
): Parcelable {
    constructor(): this("", "", "", "", "")
}