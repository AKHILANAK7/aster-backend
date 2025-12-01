package com.aster.auth

import io.vertx.ext.web.RoutingContext
import io.vertx.core.json.JsonObject

object RoleHandler {
    
    fun requireRole(vararg allowedRoles: String): (RoutingContext) -> Unit {
        return { ctx ->
            val userRole = ctx.get<String>("role")
            
            if (userRole == null) {
                ctx.response()
                    .setStatusCode(401)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject()
                        .put("error", "Authentication required")
                        .encode())
            } else if (!allowedRoles.contains(userRole)) {
                ctx.response()
                    .setStatusCode(403)
                    .putHeader("content-type", "application/json")
                    .end(JsonObject()
                        .put("error", "Access denied. Required role: ${allowedRoles.joinToString(" or ")}")
                        .put("yourRole", userRole)
                        .encode())
            } else {
                ctx.next()
            }
        }
    }
    
    fun requireAdmin(): (RoutingContext) -> Unit {
        return requireRole("admin")
    }
}
