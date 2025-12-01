package com.aster.routes

import com.aster.models.User
import com.aster.db.Collections
import com.aster.auth.JwtUtils
import io.vertx.ext.web.Router
import io.vertx.ext.mongo.MongoClient
import org.mindrot.jbcrypt.BCrypt
import io.vertx.core.json.JsonObject
import io.vertx.core.impl.logging.LoggerFactory

private val logger = LoggerFactory.getLogger("AuthRoutes")

fun mountAuthRoutes(router: Router, mongo: MongoClient, jwtProvider: io.vertx.ext.auth.jwt.JWTAuth) {

    val registerHandler = { ctx: io.vertx.ext.web.RoutingContext ->
        try {
            val body = ctx.body().asJsonObject()
            if (body == null) {
                ctx.response()
                    .setStatusCode(400)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject().put("error", "Request body is required").encode())
            } else {
                // Support both 'username' and 'email' fields
                val username = body.getString("username") ?: body.getString("email")
                val password = body.getString("password")
                val role = body.getString("role") ?: "customer"

                if (username.isNullOrBlank() || password.isNullOrBlank()) {
                    ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(JsonObject().put("error", "Username and password are required").encode())
                } else {
                    // Check if user already exists
                    println("DEBUG: Checking if user exists: $username")
                    mongo.findOne(Collections.USERS, JsonObject().put("username", username), null) { findResult ->
                        if (findResult.succeeded() && findResult.result() != null) {
                            println("DEBUG: User already exists: $username")
                            ctx.response()
                                .setStatusCode(409)
                                .putHeader("content-type", "application/json")
                                .end(JsonObject().put("error", "User already exists").encode())
                        } else if (findResult.failed()) {
                            println("DEBUG: Database error checking user existence: ${findResult.cause().message}")
                            ctx.response()
                                .setStatusCode(500)
                                .putHeader("content-type", "application/json")
                                .end(JsonObject().put("error", "Database error").encode())
                        } else {
                            println("DEBUG: User does not exist, proceeding with registration")
                            try {
                                val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
                                println("DEBUG: Password hashed")
                                
                                // Manually create JSON to avoid mapping issues and ensure _id is NOT included
                                val userJson = JsonObject()
                                    .put("username", username)
                                    .put("email", username)
                                    .put("password", hashed)
                                    .put("role", role)
                                
                                println("DEBUG: Inserting user JSON: $userJson")

                                mongo.insert(Collections.USERS, userJson) { ar ->
                                    if (ar.succeeded()) {
                                        println("DEBUG: User inserted into MongoDB with ID: ${ar.result()}")
                                        ctx.response()
                                            .setStatusCode(201)
                                            .putHeader("content-type", "application/json")
                                            .end(JsonObject()
                                                .put("message", "Registration successful")
                                                .put("username", username)
                                                .encode())
                                    } else {
                                        println("DEBUG: Failed to insert user into MongoDB: ${ar.cause().message}")
                                        ar.cause().printStackTrace()
                                        ctx.response()
                                            .setStatusCode(500)
                                            .putHeader("content-type", "application/json")
                                            .end(JsonObject().put("error", "Registration failed: ${ar.cause().message}").encode())
                                    }
                                }
                            } catch (e: Exception) {
                                println("DEBUG: Exception during user creation: ${e.message}")
                                e.printStackTrace()
                                ctx.response()
                                    .setStatusCode(500)
                                    .putHeader("content-type", "application/json")
                                    .end(JsonObject().put("error", "Internal server error: ${e.message}").encode())
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Exception in register handler: ${e.message}")
            e.printStackTrace()
            ctx.response()
                .setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(JsonObject().put("error", "Invalid request body").encode())
        }
    }

    router.post("/api/auth/register").handler { ctx -> registerHandler(ctx) }
    router.post("/api/auth/signup").handler { ctx -> registerHandler(ctx) }


    router.post("/api/auth/login").handler { ctx ->
        try {
            val body = ctx.body().asJsonObject()
            if (body == null) {
                 ctx.response()
                    .setStatusCode(400)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject().put("error", "Request body is required").encode())
            } else {
                // Support both 'username' and 'email' fields
                val username = body.getString("username") ?: body.getString("email")
                val password = body.getString("password")

                println("DEBUG: Login attempt for user: $username")

                if (username.isNullOrBlank() || password.isNullOrBlank()) {
                    ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end(JsonObject().put("error", "Username and password are required").encode())
                } else {
                    mongo.findOne(Collections.USERS, JsonObject().put("username", username), null) { ar ->
                        if (ar.succeeded() && ar.result() != null) {
                            val userDoc = ar.result()
                            val hashed = userDoc.getString("password")

                            if (hashed != null && BCrypt.checkpw(password, hashed)) {
                                println("DEBUG: Login successful for user: $username")
                                val role = userDoc.getString("role") ?: "customer"
                                val token = JwtUtils.generateToken(
                                    jwtProvider,
                                    username,
                                    role
                                )

                                // Return token along with user info
                                ctx.response()
                                    .putHeader("content-type", "application/json")
                                    .end(JsonObject()
                                        .put("token", token)
                                        .put("user", JsonObject()
                                            .put("username", username)
                                            .put("role", role))
                                        .encode())
                            } else {
                                println("DEBUG: Invalid password for user: $username")
                                ctx.response()
                                    .setStatusCode(401)
                                    .putHeader("content-type", "application/json")
                                    .end(JsonObject().put("error", "Invalid credentials").encode())
                            }

                        } else {
                            println("DEBUG: User not found: $username")
                            ctx.response()
                                .setStatusCode(404)
                                .putHeader("content-type", "application/json")
                                .end(JsonObject().put("error", "User not found").encode())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("DEBUG: Exception in login handler: ${e.message}")
            e.printStackTrace()
            ctx.response()
                .setStatusCode(400)
                .putHeader("content-type", "application/json")
                .end(JsonObject().put("error", "Invalid request body").encode())
        }
    }
}