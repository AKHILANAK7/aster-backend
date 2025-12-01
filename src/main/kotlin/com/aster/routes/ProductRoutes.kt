package com.aster.routes

import com.aster.models.Product
import com.aster.db.Collections
import com.aster.auth.AuthHandler
import com.aster.auth.RoleHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.json.JsonArray
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.core.impl.logging.LoggerFactory

private val logger = LoggerFactory.getLogger("ProductRoutes")

fun mountProductRoutes(router: Router, mongo: MongoClient, jwtProvider: JWTAuth) {

    router.get("/api/products").handler { ctx ->
        try {
            mongo.find(Collections.PRODUCTS, JsonObject()) { result ->
                if (result.succeeded()) {
                    val products = result.result() ?: emptyList()
                    // Return array directly, not wrapped in data object
                    ctx.response()
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json")
                        .end(JsonArray(products).encodePrettily())
                } else {
                    val error = result.cause()
                    logger.error("Failed to fetch products", error)
                    sendError(ctx, 500, "Failed to fetch products: ${error.message}")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error in GET /api/products", e)
            sendError(ctx, 500, "Internal server error")
        }
    }


    router.post("/api/products")
        .handler(AuthHandler.requireAuth(jwtProvider))
        .handler(RoleHandler.requireAdmin())
        .handler { ctx ->
        try {
            val body = try {
                ctx.body().asJsonObject()
            } catch (e: Exception) {
                logger.error("Failed to parse request body", e)
                sendError(ctx, 400, "Invalid JSON in request body")
                return@handler
            }
            
            if (body == null || body.isEmpty) {
                sendError(ctx, 400, "Request body is required")
                return@handler
            }

            // Validate required fields
            val errors = mutableListOf<String>()
            val name = body.getString("name")
            val price = body.getDouble("price")
            val stock = body.getInteger("stock")
            val description = body.getString("description", "")

            if (name.isNullOrBlank()) errors.add("Name is required")
            if (price == null) errors.add("Price is required")
            if (price != null && price <= 0) errors.add("Price must be greater than 0")
            if (stock == null) errors.add("Stock is required")
            if (stock != null && stock < 0) errors.add("Stock cannot be negative")

            if (errors.isNotEmpty()) {
                sendError(ctx, 400, "Validation failed", json { obj("errors" to JsonArray(errors)) })
                return@handler
            }

            val product = Product(
                name = name!!,
                price = price!!,
                stock = stock!!,
                description = description
            )

            mongo.insert(Collections.PRODUCTS, JsonObject.mapFrom(product)) { result ->
                if (result.succeeded()) {
                    val id = result.result()
                    val username = ctx.get<String>("username") ?: "unknown"
                    logger.info("Product created by admin: $username")
                    sendJson(
                        ctx = ctx,
                        statusCode = 201,
                        body = json {
                            obj(
                                "message" to "Product created successfully",
                                "id" to id
                            )
                        }
                    )
                } else {
                    val error = result.cause()
                    logger.error("Failed to create product", error)
                    sendError(ctx, 500, "Failed to create product: ${error.message}")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error in POST /api/products", e)
            sendError(ctx, 500, "Internal server error")
        }
    }

    // ===============================
    // GET /api/products/:id  (Get single product)
    // ===============================
    router.get("/api/products/:id").handler { ctx ->
        try {
            val id = ctx.pathParam("id")
            if (id.isBlank()) {
                sendError(ctx, 400, "Product ID is required")
                return@handler
            }

            val query = JsonObject().put("_id", id)
            mongo.findOne(Collections.PRODUCTS, query, null) { result ->
                if (result.succeeded()) {
                    val product = result.result()
                    if (product == null) {
                        sendError(ctx, 404, "Product not found")
                    } else {
                        sendJson(ctx, 200, json { obj("data" to product) })
                    }
                } else {
                    val error = result.cause()
                    logger.error("Failed to fetch product $id", error)
                    sendError(ctx, 500, "Failed to fetch product")
                }
            }
        } catch (e: Exception) {
            logger.error("Unexpected error in GET /api/products/:id", e)
            sendError(ctx, 500, "Internal server error")
        }
    }
}

private fun sendJson(ctx: RoutingContext, statusCode: Int, body: JsonObject) {
    ctx.response()
        .setStatusCode(statusCode)
        .putHeader("Content-Type", "application/json")
        .end(body.encodePrettily())
}

private fun sendError(ctx: RoutingContext, statusCode: Int, message: String, details: JsonObject? = null) {
    val errorResponse = JsonObject()
        .put("status", "error")
        .put("message", message)
    
    if (details != null) {
        errorResponse.put("details", details)
    }
    
    ctx.response()
        .setStatusCode(statusCode)
        .putHeader("Content-Type", "application/json")
        .end(errorResponse.encodePrettily())
}
