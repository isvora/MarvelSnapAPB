import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.extractIt
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import mu.KotlinLogging

data class MyDataClass(
    var httpStatusCode: Int = 0,
    var allLinks: List<DocElement> = emptyList()
)

class HtmlExtractionService {

    private val logger = KotlinLogging.logger {}

    fun extract(url: String, cssQuery: String) : List<DocElement> {
        val extracted = skrape(HttpFetcher) {
            request {
                this.url = url
            }

            extractIt<MyDataClass> {
                it.httpStatusCode = responseStatus.code
                htmlDocument {
                    it.allLinks = allElements
                }
            }
        }

        if (extracted.httpStatusCode != 200) {
            logger.error { url + " returned status code " + extracted.httpStatusCode }
            throw Exception()
        }

        return extracted.allLinks.filter { it.element.`is`(cssQuery) };
    }

}