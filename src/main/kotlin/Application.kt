import spark.kotlin.*
import java.sql.*

class Application

fun connection(): Connection = DriverManager.getConnection("jdbc:h2:mem:spark-kotlin")

fun main(args: Array<String>) {
  val http: Http = ignite()

  http.port(8080)

  http.get("/status") {
    val conn = connection()
    try {
      val status = if (conn.isClosed) "closed" else "open"
      "db connection: ${status}"
    } finally {
      conn.close()
    }
  }
}
