package com.aster.routes

import com.aster.db.Collections
import com.aster.auth.JwtUtils
import io.vertx.ext.web.Router
import io.vertx.ext.mongo.MongoClient
import org.mindrot.jbcrypt.BCrypt
import io.vertx.core.json.JsonObject

fun mountAuthRoutes(router: Router, mongo: MongoClient, jwt: io.vertx.ext.auth.jwt.JWTAuth) {

    // REGISTER
    router.post("/api/auth/register").handler { ctx ->
        val body = ctx.body().asJsonObject()

        val email = body.getString("email")
        val password = body.getString("password")
        val requestedRole = body.getString("role")?.lowercase()
        val allowedRoles = setOf("admin", "customer")
        val role = if (requestedRole != null && allowedRoles.contains(requestedRole)) {
            requestedRole
        } else {
            "customer"
        }

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            ctx.response().setStatusCode(400)
                .end(JsonObject().put("error", "Email & password required").encode())
            return@handler
        }

        mongo.findOne(Collections.USERS, JsonObject().put("email", email), null) { res ->
            if (res.result() != null) {
                ctx.response().setStatusCode(409)
                    .end(JsonObject().put("error", "Email already exists").encode())
                return@findOne
            }

            val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
            val userJson = JsonObject()
                .put("email", email)
                .put("password", hashed)
                .put("role", role)

            mongo.insert(Collections.USERS, userJson) {
                ctx.response().setStatusCode(201)
                    .end(JsonObject().put("message", "Registration successful").encode())
            }
        }
    }


    // LOGIN
    router.post("/api/auth/login").handler { ctx ->
        val body = ctx.body().asJsonObject()

        val email = body.getString("email")
        val password = body.getString("password")

        mongo.findOne(Collections.USERS, JsonObject().put("email", email), null) { ar ->
            val user = ar.result() ?: run {
                ctx.response().setStatusCode(404)
                    .end(JsonObject().put("error", "User not found").encode())
                return@findOne
            }

            val hashed = user.getString("password")

            if (!BCrypt.checkpw(password, hashed)) {
                ctx.response().setStatusCode(401)
                    .end(JsonObject().put("error", "Invalid credentials").encode())
                return@findOne
            }

            val role = user.getString("role") ?: "customer"
            val token = JwtUtils.generateToken(jwt, email, role)

            ctx.response().putHeader("content-type", "application/json")
                .end(JsonObject()
                    .put("token", token)
                    .put("user", JsonObject().put("email", email).put("role", role))
                    .encode())
        }
    }
}