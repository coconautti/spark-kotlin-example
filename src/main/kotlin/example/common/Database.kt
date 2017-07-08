package example.common

import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.impl.DSL
import java.sql.Connection
import java.sql.DriverManager

object Database {
    val jdbcConnectionUrl = "jdbc:h2:file:./spark-kotlin"

    fun migrate(clean: Boolean = false) {
        val flyway = Flyway()
        flyway.setDataSource(jdbcConnectionUrl, "", "")

        // DANGER ZONE: Do not run in production!!!
        if (clean) {
            flyway.clean()
        }

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
