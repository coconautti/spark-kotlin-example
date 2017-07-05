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

  fun withConnection(body: (Connection) -> Unit) {
    val conn = connection()
    try {
      body(conn)
    } finally {
      conn.close()
    }
  }
}

class Application

fun main(args: Array<String>) {
  Database.migrate()

  val http: Http = ignite()
  http.port(8080)

  http.get("/status") {
    var response = ""
    Database.withConnection { conn ->
      val status = if (conn.isClosed) "closed" else "open"
      response += "db test connection: ${status} \n"

      val stmt = conn.createStatement()
      val rs = stmt.executeQuery("SELECT * FROM users")
      response += "db migrated: ${!rs.next()} \n"
    }
    response
  }
}
