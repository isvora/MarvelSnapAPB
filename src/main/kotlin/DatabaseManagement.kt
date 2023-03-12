import mu.KotlinLogging
import java.sql.DriverManager
import java.util.Properties

private val logger = KotlinLogging.logger {}

data class Article(val id: Int, val title: String, val url: String)

class DatabaseManagement {

    fun updateArticles(articles: List<Article>) {

        val username = getProp("username")
        val password = getProp("password")

        val jdbcUrl =
            "jdbc:postgresql://db.zpkmeupkybtmjfhliqwv.supabase.co:5432/postgres?user=${username}&password=${password}"

        val connection = DriverManager
            .getConnection(jdbcUrl, username, username)

        if (!connection.isValid(0)) {
            throw RuntimeException("DB Connection is not valid")
        }

        articles.forEach {
            val query = connection
                .prepareStatement("INSERT INTO articles(id, title, url)\n VALUES(${it.id}, \'${it.title}\', \'${it.url}\');")
            query.executeUpdate()
        }
    }

    private fun getProp(key: String) : String {
        val props  = javaClass.classLoader.getResourceAsStream("marvelsnapapb.config").use {
            Properties().apply { load(it) }
        }
        return (props.getProperty(key)) ?: throw RuntimeException("could not find property $key")
    }
}
