package com.example.admincontroll.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String? = null,
    val city: String? = null,
    val phone: String? = null,
    val state: String? = null,
    val street: String? = null
): Parcelable
