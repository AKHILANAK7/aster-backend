package com.aster

import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import com.aster.routes.mountAuthRoutes
import com.aster.auth.JwtUtils
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import com.aster.db.MongoClientProvider
import com.aster.routes.mountProductRoutes
import io.vertx.core.impl.logging.LoggerFactory


class MainVerticle : AbstractVerticle() {
    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

    override fun start() {
        try {
            val jwtProvider = JwtUtils.createProvider(vertx)



            val mongoUri = System.getenv("MONGODB_URI") ?: "mongodb://localhost:27017/aster"
            val mongo = MongoClientProvider.create(vertx, mongoUri)
            
            // Test the MongoDB connection
            mongo.runCommand("ping", JsonObject().put("ping", 1)) { res ->
                if (res.failed()) {
                    logger.error(" Failed to connect to MongoDB", res.cause())
                    vertx.close()
                    return@runCommand
                }
                logger.info("✅ Connected to MongoDB")
            }

            val router = Router.router(vertx)

            router.route().handler { ctx ->
                println("DEBUG: Incoming request: ${ctx.request().method()} ${ctx.request().uri()}")
                ctx.response()
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization")
                    .putHeader("Access-Control-Max-Age", "3600")
                
                if (ctx.request().method().name() == "OPTIONS") {
                    ctx.response().end()
                } else {
                    ctx.next()
                }
            }

            // Global failure handler
            router.route().failureHandler { ctx ->
                println("DEBUG: Global failure handler caught error: ${ctx.failure()?.message}")
                ctx.failure()?.printStackTrace()
                ctx.next() // Allow default failure handler to proceed or send response
            }

            router.route().handler(BodyHandler.create())

            mountProductRoutes(router, mongo, jwtProvider)
            mountAuthRoutes(router, mongo, jwtProvider)

            router.get("/api/health").handler { ctx ->
                ctx.response()
                    .putHeader("content-type", "application/json")
                    .end(JsonObject().put("status", "UP").encodePrettily())
            }

            // Start HTTP server
            val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
            
            vertx.createHttpServer()
                .requestHandler(router)
                .listen(port) { http ->
                    if (http.succeeded()) {
                        logger.info("🚀 Server running on http://localhost:$port")
                    } else {
                        logger.error("❌ Failed to start server", http.cause())
                    }
                }

        } catch (e: Exception) {
            logger.error("❌ Failed to start application", e)
            vertx.close()
        }
    }
}

fun main() {
    val vertx = Vertx.vertx()
    
    vertx.exceptionHandler { throwable ->
        LoggerFactory.getLogger("GlobalExceptionHandler").error("Unhandled exception", throwable)
    }
    
    vertx.deployVerticle(MainVerticle::class.java.name) { res ->
        if (res.failed()) {
            println("Failed to deploy verticle: ${res.cause()}")
            vertx.close()
        }
    }
}