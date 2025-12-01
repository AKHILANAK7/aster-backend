package com.aster.models

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
    val _id: String? = null,
    val username: String,
    val email: String? = null,  // Optional for backward compatibility
    val password: String,
    val role: String = "customer"   // customer or admin
)