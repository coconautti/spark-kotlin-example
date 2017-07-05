import spark.kotlin.*

class Application

fun main(args: Array<String>) {
  val http: Http = ignite()

  http.port(8080)

  http.get("/hello") {
    "Hello, world!"
  }
}
