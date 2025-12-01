package com.aster.db

import io.vertx.ext.mongo.MongoClient
import io.vertx.core.json.JsonObject
import io.vertx.core.Vertx

object MongoClientProvider {

    fun create(vertx: Vertx, uri: String): MongoClient {
        val config = JsonObject()
            .put("connection_string", uri)
            .put("db_name", "aster")

        return MongoClient.createShared(vertx, config)
    }
}