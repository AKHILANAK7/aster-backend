package com.aster.models

data class CartItem(
    val productId: String,
    val quantity: Int
)

data class Cart(
    val _id: String? = null,
    val userId: String,
    val items: List<CartItem>
)