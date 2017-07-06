import com.fasterxml.jackson.databind.*
import java.sql.*
import org.flywaydb.core.*
import spark.kotlin.*

object Database {
    val jdbcConnectionUrl = "jdbc:h2:file:./spark-kotlin"

    fun migrate() {
        val flyway = Flyway()
        flyway.setDataSource(jdbcConnectionUrl, "", "")
        flyway.clean() // <-- DANGER ZONE: DO NOT RUN IN PRODUCTION
        flyway.migrate()
    }

    fun connection(): Connection = DriverManager.getConnection(jdbcConnectionUrl)

    fun withConnection(body: (Connection) -> Any): Any {
        val conn = connection()
        try {
            return body(conn)
        } finally {
            conn.close()
        }
    }
}

object Json {
    val objectMapper = ObjectMapper()

    fun toJson(model: Any?): String = objectMapper.writeValueAsString(model)
}

data class StatusReponse(val connectionStatus: String, val migrationStatus: Boolean)

class Application

fun main(args: Array<String>) {
    Database.migrate()

    val http: Http = ignite()
    http.port(8080)

    http.after {
        response.header("Content-Type", "application/json")
    }

    http.get("/status") {
        Database.withConnection { conn ->
            val connectionStatus = if (conn.isClosed) "closed" else "open"
            val migrationStatus = !conn.createStatement().executeQuery("SELECT * FROM users").next()
            Json.toJson(StatusReponse(connectionStatus, migrationStatus))
        }
    }
}
