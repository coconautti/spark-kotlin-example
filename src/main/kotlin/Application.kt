import com.fasterxml.jackson.databind.*
import java.sql.*
import org.flywaydb.core.*
import spark.kotlin.*
import org.jooq.impl.DSL
import org.jooq.*

object Database {
    val jdbcConnectionUrl = "jdbc:h2:file:./spark-kotlin"

    fun migrate() {
        val flyway = Flyway()
        flyway.setDataSource(jdbcConnectionUrl, "", "")
        flyway.clean() // <-- DANGER ZONE: DO NOT RUN IN PRODUCTION
        flyway.migrate()
    }

    fun connection(): Connection = DriverManager.getConnection(jdbcConnectionUrl)

    inline fun withConnection(body: (Connection) -> Any): Any {
        val conn = connection()
        try {
            return body(conn)
        } finally {
            conn.close()
        }
    }

    fun query(): DSLContext = DSL.using(jdbcConnectionUrl)

    inline fun withQuery(body: (DSLContext) -> Any): Any = body(query())
}

object Json {
    val objectMapper = ObjectMapper()

    fun toJson(model: Any?): String = objectMapper.writeValueAsString(model)
}

data class StatusReponse(val dbConnection: String, val dbMigrated: Boolean)

class Application

fun main(args: Array<String>) {
    Database.migrate()

    val http: Http = ignite()
    http.port(8080)

    http.after {
        response.header("Content-Type", "application/json")
    }

    http.get("/status") {
        val dbConnection = Database.withConnection { conn -> if (conn.isClosed) "fail" else "ok" } as String
        val dbMigrated = Database.withQuery { query -> query.select().from("users").fetch().size >= 0 } as Boolean
        Json.toJson(StatusReponse(dbConnection, dbMigrated))
    }
}
