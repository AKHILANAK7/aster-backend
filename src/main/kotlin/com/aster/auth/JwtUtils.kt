package com.aster.auth

import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.auth.JWTOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject

object JwtUtils {

    fun createProvider(vertx: Vertx): JWTAuth {
        return JWTAuth.create(
            vertx,
            JWTAuthOptions(
                JsonObject()
                    .put("keyStore", JsonObject()
                        .put("path", "keystore.jceks")
                        .put("type", "jceks")
                        .put("password", "secret")
                    )
            )
        )
    }

    fun generateToken(provider: JWTAuth, email: String, role: String): String {
        return provider.generateToken(
            JsonObject()
                .put("email", email)
                .put("role", role),
            JWTOptions().setAlgorithm("RS256")
        )
    }
}