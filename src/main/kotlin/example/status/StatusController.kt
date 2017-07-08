package example.status

import example.common.Database
import example.common.Json
import spark.kotlin.get

data class Status(val dbConnection: String, val dbMigrated: Boolean)

class StatusController {

    init {
        get("/status") {
            val dbConnection = Database.withConnection { conn -> if (conn.isClosed) "fail" else "ok" } as String
            val dbMigrated = Database.withQuery { query -> query.select().from("users").fetch().size >= 0 } as Boolean
            Json.toJson(Status(dbConnection, dbMigrated))
        }
    }
}