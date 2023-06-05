package com.example.admincontroll.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val name: String,
    val category: String,
    val price: Float,
    val description: String? = null,
    val image: List<String>,
    val adminIdEmail: String
): Parcelable {
    constructor(): this("","", 0f, "", emptyList(), "")
}
