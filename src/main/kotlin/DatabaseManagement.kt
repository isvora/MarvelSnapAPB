import java.sql.Connection
import java.sql.DriverManager
import java.util.*

data class Article(val id: Int, val title: String, val url: String) : Comparable<Article> {
    override fun compareTo(other: Article): Int {
        return compareValuesBy(this, other) { it.id }
    }
}

class DatabaseManagement {

    private val username = getProp("username")
    private val password = getProp("password")

    private val jdbcUrl =
        "jdbc:postgresql://db.zpkmeupkybtmjfhliqwv.supabase.co:5432/postgres?user=${username}&password=${password}"

    private val connection: Connection = DriverManager
        .getConnection(jdbcUrl, username, username)

    fun getArticles() : List<Article> {
        checkDBConnection(connection)

        val query = connection.prepareStatement("SELECT * FROM articles")
        val result = query.executeQuery()
        val existingArticles = mutableListOf<Article>()

        while (result.next()) {
            existingArticles.add(Article(result.getInt("id"),
                result.getString("title"),
                result.getString("url")))
        }

        return existingArticles
    }

    fun updateArticles(articles: List<Article>) {
        checkDBConnection(connection)

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

    private fun checkDBConnection(connection: Connection) {
        if (!connection.isValid(0)) {
            throw RuntimeException("DB Connection is not valid")
        }
    }
}
