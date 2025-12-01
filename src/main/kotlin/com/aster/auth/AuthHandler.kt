package com.aster.auth

import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.core.json.JsonObject

object AuthHandler {
    
    fun requireAuth(jwtProvider: JWTAuth): (RoutingContext) -> Unit {
        return { ctx ->
            try {
                val authHeader = ctx.request().getHeader("Authorization")
                
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    ctx.response()
                        .setStatusCode(401)
                        .putHeader("content-type", "application/json")
                        .end(JsonObject()
                            .put("error", "Missing or invalid Authorization header")
                            .encode())
                } else {
                    val token = authHeader.substring(7) // Remove "Bearer " prefix
                    
                    jwtProvider.authenticate(JsonObject().put("jwt", token)) { authResult ->
                        if (authResult.succeeded()) {
                            val user = authResult.result()
                            val principal = user.principal()
                            
                            // Attach user info to context for use in handlers
                            ctx.put("username", principal.getString("username") ?: principal.getString("email"))
                            ctx.put("role", principal.getString("role") ?: "customer")
                            
                            ctx.next()
                        } else {
                            ctx.response()
                                .setStatusCode(401)
                                .putHeader("content-type", "application/json")
                                .end(JsonObject()
                                    .put("error", "Invalid or expired token")
                                    .encode())
                        }
                    }
                }
            } catch (e: Exception) {
                ctx.response()
                    .setStatusCode(401)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject()
                        .put("error", "Authentication failed: ${e.message}")
                        .encode())
            }
        }
    }
}

