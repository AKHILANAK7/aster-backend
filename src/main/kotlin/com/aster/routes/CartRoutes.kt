package com.aster.routes

import com.aster.models.Cart
import com.aster.models.CartItem
import com.aster.auth.AuthHandler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.Router
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.auth.jwt.JWTAuth
import org.bson.types.ObjectId

class CartRoutes(
    private val router: Router,
    private val mongo: MongoClient,
    private val jwtAuth: JWTAuth
) {

    fun setupRoutes() {

        // 🔐 All cart routes need JWT
        router.route("/api/cart/*").handler(AuthHandler.requireAuth(jwtAuth))

        // ⭐ GET CART
        router.get("/api/cart").handler { ctx ->
            val userId = ctx.user().principal().getString("email")

            mongo.findOne("users", JsonObject().put("email", userId), JsonObject()) { userRes ->
                if (userRes.failed()) {
                    ctx.response().setStatusCode(500).end("""{"message": "Failed to find user"}""")
                    return@findOne
                }
                val user = userRes.result()
                if (user == null) {
                    ctx.response().setStatusCode(404).end("""{"message": "User not found"}""")
                    return@findOne
                }
                val userDbId = user.getString("_id")

                mongo.findOne("carts", JsonObject().put("userId", userDbId), JsonObject()) { cartRes ->
                    if (cartRes.failed()) {
                        ctx.response().setStatusCode(500).end("""{"message": "Failed to find cart"}""")
                        return@findOne
                    }
                    val cart = cartRes.result()
                    if (cart == null) {
                        ctx.response()
                            .putHeader("content-type", "application/json")
                            .end("""{"items": []}""")
                        return@findOne
                    }

                    val items = cart.getJsonArray("items")
                    if (items == null || items.isEmpty) {
                         ctx.response()
                            .putHeader("content-type", "application/json")
                            .end(cart.encode())
                        return@findOne
                    }

                    val productIds = JsonArray()
                    items.forEach { item ->
                        val jsonItem = item as JsonObject
                        productIds.add(jsonItem.getString("productId"))
                    }

                    val query = JsonObject().put("_id", JsonObject().put("\$in", productIds))
                    
                    mongo.find("products", query) { productsRes ->
                        if (productsRes.succeeded()) {
                            val products = productsRes.result()
                            val populatedItems = JsonArray()

                            items.forEach { item ->
                                val jsonItem = item as JsonObject
                                val product = products.find { it.getString("_id") == jsonItem.getString("productId") }
                                if (product != null) {
                                    // Create a new object to avoid modifying the original cart in DB (though we are just reading)
                                    // Merge product details into item. 
                                    // Product has _id, name, price, etc.
                                    // Item has productId, quantity.
                                    // We want the result to have _id (from product), name, price, quantity.
                                    val populatedItem = jsonItem.copy().mergeIn(product)
                                    populatedItems.add(populatedItem)
                                }
                            }
                            
                            cart.put("items", populatedItems)
                            ctx.response()
                                .putHeader("content-type", "application/json")
                                .end(cart.encode())
                        } else {
                            ctx.response()
                                .setStatusCode(500)
                                .end("""{"message": "Failed to fetch products"}""")
                        }
                    }
                }
            }
        }

        // ⭐ ADD TO CART
        router.post("/api/cart/add").handler { ctx ->
            ctx.request().bodyHandler { buffer ->

                val body = buffer.toJsonObject()
                val productId = body.getString("productId")
                val quantity = body.getInteger("quantity", 1)

                val userEmail = ctx.user().principal().getString("email")

                // Get user ID
                mongo.findOne("users", JsonObject().put("email", userEmail), JsonObject()) { userRes ->
                    val user = userRes.result()
                    val userId = user.getString("_id")

                    // Check if user already has a cart
                    mongo.findOne("carts", JsonObject().put("userId", userId), JsonObject()) { cartRes ->
                        val cart = cartRes.result()

                        if (cart == null) {
                            // CREATE NEW CART
                            val newCart = JsonObject()
                                .put("userId", userId)
                                .put("items", listOf(
                                    JsonObject()
                                        .put("productId", productId)
                                        .put("quantity", quantity)
                                ))

                            mongo.insert("carts", newCart) {
                                ctx.response().setStatusCode(201).end("""{"message":"Item added"}""")
                            }

                        } else {
                            // UPDATE EXISTING CART
                            val items = cart.getJsonArray("items").map { it as JsonObject }.toMutableList()

                            val existingItem = items.find { it.getString("productId") == productId }

                            if (existingItem != null) {
                                existingItem.put("quantity", existingItem.getInteger("quantity") + quantity)
                            } else {
                                items.add(
                                    JsonObject()
                                        .put("productId", productId)
                                        .put("quantity", quantity)
                                )
                            }

                            val update = JsonObject().put("\$set", JsonObject().put("items", items))

                            mongo.updateCollection("carts",
                                JsonObject().put("_id", cart.getString("_id")),
                                update
                            ) {
                                ctx.response().end("""{"message":"Item updated"}""")
                            }
                        }
                    }
                }
            }
        }

        // ⭐ REMOVE ITEM
        router.post("/api/cart/remove").handler { ctx ->
            ctx.request().bodyHandler { buffer ->
                val body = buffer.toJsonObject()
                val productId = body.getString("productId")
                val email = ctx.user().principal().getString("email")

                mongo.findOne("users", JsonObject().put("email", email), JsonObject()) { userRes ->
                    val userId = userRes.result().getString("_id")

                    mongo.findOne("carts", JsonObject().put("userId", userId), JsonObject()) { cartRes ->
                        val cart = cartRes.result()
                        if (cart == null) {
                            ctx.response().end("""{"items": []}""")
                            return@findOne
                        }

                        val items = cart.getJsonArray("items").map { it as JsonObject }.toMutableList()
                        items.removeIf { it.getString("productId") == productId }

                        val update = JsonObject().put("\$set", JsonObject().put("items", items))

                        mongo.updateCollection("carts",
                            JsonObject().put("_id", cart.getString("_id")),
                            update
                        ) {
                            ctx.response().end("""{"message":"Item removed"}""")
                        }
                    }
                }
            }
        }

        // ⭐ CLEAR CART
        router.post("/api/cart/clear").handler { ctx ->
            val email = ctx.user().principal().getString("email")

            mongo.findOne("users", JsonObject().put("email", email), JsonObject()) { userRes ->
                val userId = userRes.result().getString("_id")

                mongo.removeDocument("carts", JsonObject().put("userId", userId)) {
                    ctx.response().end("""{"message":"Cart cleared"}""")
                }
            }
        }
    }
}