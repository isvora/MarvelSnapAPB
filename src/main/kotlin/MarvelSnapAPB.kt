import it.skrape.selects.DocElement
import mu.KotlinLogging
import org.jsoup.nodes.TextNode
import java.util.*

private val logger = KotlinLogging.logger {}
private const val url = "https://marvelsnapzone.com/articles/"
private const val cssQuery = "h2.entry-title"

fun main() {
    logger.info { "Starting MarvelSnapAPB" }

    val service = HtmlExtractionService()
    val links = service.extract(url, cssQuery)

    val articles = prepareArticles(links)

    val db = DatabaseManagement()
    val existingArticles = db.getArticles()

    val newArticles = checkForNewArticles(existingArticles, articles)

    if (newArticles.isNotEmpty()) {
        // TODO update the db to reflect the changes
        // TODO get the new article, upload it to reddit.
    }
}

private fun prepareArticles(links: List<DocElement>) : List<Article> {
    val articles = mutableListOf<Article>()

    var id = 1
    links.forEach {
        val child = it.element.childNodes()[1]

        // Marvel snap zone returns title with a '\n' in the beginning.
        // We use removeRange(0,1) to get rid of it.
        articles.add(Article(id,
            (child.childNodes()[0] as TextNode).wholeText.removeRange(0, 1),
            child.attributes().get("href")))
        id++
    }

    return articles
}

private fun checkForNewArticles(existingArticles: List<Article>, articles: List<Article>) : List<Article> {
    Collections.sort(existingArticles)
    Collections.sort(articles)
    return if (existingArticles != articles) {
        val newArticles = articles.minus(existingArticles.toSet())
        logger.info { "New articles found: $newArticles" }
        newArticles
    } else {
        emptyList()
    }
}

