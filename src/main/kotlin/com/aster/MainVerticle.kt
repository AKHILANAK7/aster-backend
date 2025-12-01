package com.aster

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import com.aster.auth.JwtUtils
import com.aster.routes.mountAuthRoutes
import com.aster.routes.mountProductRoutes
import com.aster.routes.CartRoutes
import com.aster.db.MongoClientProvider

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.mongo.MongoClient
import io.vertx.core.impl.logging.LoggerFactory

class MainVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

    override fun start() {

        try {
            // -----------------------------------------
            // 1) JWT Provider
            // -----------------------------------------
            val jwtProvider = JwtUtils.createProvider(vertx)

            // -----------------------------------------
            // 2) MongoDB Connection
            // -----------------------------------------
            val mongoUri = System.getenv("MONGODB_URI")
                ?: "mongodb://localhost:27017/aster"

            val mongo: MongoClient = MongoClientProvider.create(vertx, mongoUri)

            // Ping MongoDB
            mongo.runCommand("ping", JsonObject().put("ping", 1)) { res ->
                if (res.failed()) {
                    logger.error("❌ Failed to connect to MongoDB", res.cause())
                    vertx.close()
                    return@runCommand
                }
                logger.info("📦 Connected to MongoDB")
            }

            // -----------------------------------------
            // 3) Router
            // -----------------------------------------
            val router = Router.router(vertx)

            // -----------------------------------------
            // 4) CORS Handler + Preflight
            // -----------------------------------------
            router.route().handler { ctx ->
                ctx.response()
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
                    .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")

                // CORS Preflight
                if (ctx.request().method().name() == "OPTIONS") {
                    ctx.response().setStatusCode(204).end()
                } else {
                    ctx.next()
                }
            }

            // -----------------------------------------
            // 5) BodyHandler (MUST BE BEFORE ROUTES)
            // -----------------------------------------
            router.route().handler(BodyHandler.create())

            // -----------------------------------------
            // 6) Mount Routes (IMPORTANT ORDER)
            // -----------------------------------------
            logger.info("🔐 Mounting AuthRoutes...")
            mountAuthRoutes(router, mongo, jwtProvider)

            logger.info("📦 Mounting ProductRoutes...")
            mountProductRoutes(router, mongo, jwtProvider)

            logger.info("🛒 Mounting CartRoutes...")
            CartRoutes(router, mongo, jwtProvider).setupRoutes()

            // -----------------------------------------
            // 7) Health Route
            // -----------------------------------------
            router.get("/api/health").handler { ctx ->
                ctx.response().putHeader("content-type", "application/json")
                    .end(JsonObject().put("status", "UP").encode())
            }

            // -----------------------------------------
            // 8) Global Error Handler
            // -----------------------------------------
            router.route().failureHandler { ctx ->
                val failure = ctx.failure()
                logger.error("🔥 ERROR at ${ctx.request().path()}", failure)

                ctx.response()
                    .setStatusCode(ctx.statusCode())
                    .putHeader("content-type", "application/json")
                    .end(
                        JsonObject()
                            .put("status", "error")
                            .put("message", failure?.message ?: "Internal Server Error")
                            .put("path", ctx.request().path())
                            .encode()
                    )
            }

            // -----------------------------------------
            // 9) Start Server
            // -----------------------------------------
            val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

            vertx.createHttpServer()
                .requestHandler(router)
                .listen(port) { result ->
                    if (result.succeeded()) {
                        logger.info("🚀 Server running at http://localhost:$port")
                    } else {
                        logger.error("❌ Failed to start server", result.cause())
                    }
                }

        } catch (e: Exception) {
            logger.error("❌ Application startup failed", e)
            vertx.close()
        }
    }
}

fun main() {
    val vertx = Vertx.vertx()

    vertx.exceptionHandler {
        LoggerFactory.getLogger("GlobalExceptionHandler")
            .error("🔥 UNHANDLED ERROR", it)
    }

    vertx.deployVerticle(MainVerticle::class.java.name) { res ->
        if (res.failed()) {
            println("FAILED TO DEPLOY: ${res.cause()}")
        }
    }
}