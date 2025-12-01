package com.aster.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Product(
    val _id: String? = null,      // MongoDB document _id
    val name: String,             // Product name
    val price: Double,            // Price in rupees
    val stock: Int,               // Quantity available
    val description: String? = "" // Optional
)