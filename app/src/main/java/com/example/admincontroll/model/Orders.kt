package com.example.admincontroll.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Orders(
    val address: Address?,
    val product: List<CartProduct>?,
    val date: String,
    val orderId: Long,
    val orderStatus: String,
    val totalPrice: Long
): Parcelable {
    constructor(): this(null, emptyList(), "", 0L, "", 0L)
}
