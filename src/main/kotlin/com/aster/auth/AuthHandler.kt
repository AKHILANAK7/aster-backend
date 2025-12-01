package com.aster.auth

import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.core.json.JsonObject

object AuthHandler {

    fun requireAuth(jwtProvider: JWTAuth): (RoutingContext) -> Unit {
        return { ctx ->
            val authHeader = ctx.request().getHeader("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.response()
                    .setStatusCode(401)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject().put("error", "Missing Authorization header").encode())
                // No explicit return needed
            }

            val token = authHeader.substring(7)

            jwtProvider.authenticate(JsonObject().put("jwt", token)) { auth ->
                if (auth.succeeded()) {
                    val user = auth.result()
                    ctx.setUser(user)

                    val principal = user.principal()

                    // Store useful fields
                    ctx.put("email", principal.getString("email"))
                    ctx.put("role", principal.getString("role") ?: "user")

                    ctx.next()
                } else {
                    ctx.response()
                        .setStatusCode(401)
                        .putHeader("content-type", "application/json")
                        .end(JsonObject().put("error", "Invalid or expired token").encode())
                }
            }
        }
    }
}
