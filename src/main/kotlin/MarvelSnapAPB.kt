import it.skrape.selects.DocElement
import mu.KotlinLogging
import org.jsoup.nodes.TextNode

private val logger = KotlinLogging.logger {}
private const val url = "https://marvelsnapzone.com/articles/";
private const val cssQuery = "h2.entry-title"

fun main() {
    logger.info { "Starting MarvelSnapAPB" }

    val service = HtmlExtractionService()
    val links = service.extract(url, cssQuery)

    val articles = prepareArticles(links)

    val db = DatabaseManagement()
    db.updateArticles(articles)
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

