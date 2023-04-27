package com.example.admincontroll

data class Product(
    val name: String,
    val category: String,
    val price: Float,
    val description: String? = null,
    val image: List<String>,
    val adminIdEmail: String
)
