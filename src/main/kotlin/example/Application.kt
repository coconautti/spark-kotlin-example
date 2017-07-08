package example

import example.common.Database
import spark.kotlin.*
import spark.servlet.SparkApplication
import example.status.StatusController

class ExampleApplication : SparkApplication {

    constructor() {
        Database.migrate()

        port(8080)

        after {
            response.header("Content-Type", "application/json")
        }

        StatusController()
    }

    override fun init() {
        // Empty on purpose
    }
}

class Application

fun main(args: Array<String>) {
    ExampleApplication()
}
