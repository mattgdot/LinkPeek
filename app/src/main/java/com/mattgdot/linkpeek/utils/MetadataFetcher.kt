package com.mattgdot.linkpeek.utils

import com.mattgdot.linkpeek.models.UrlMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object MetadataFetcher {
    suspend fun fetchMetadata(url: String): Result<UrlMetadata> {
        return try {
            withContext(Dispatchers.IO) {
                val con = Jsoup.connect(url)
                val doc = con.userAgent("Mozilla").get()

                var description: String? = null
                var keywords: String? = null
                var author: String? = null
                var subtitle: String? = null
                var link: String? = null

                var image: String? = null
                var linkOg: String? = null
                var titleOg: String? = null
                var descriptionOg: String? = null

                val icon: String?

                val title: String? = doc.title()

                val img1 = doc.head().select("link[href~=.*\\.(ico|png)]").first()?.attr("href")
                val img2 = doc.head().select("meta[itemprop=image]").first()?.attr("itemprop")

                icon = img1 ?: img2 ?: ""

                val regularMetaTags = doc.select("meta")
                val ogTags = doc.select("meta[property^=og:]")

                // Process regular meta tags
                regularMetaTags.forEach { tag ->
                    val name = tag.attr("name")
                    when (name) {
                        "description" -> description = tag.attr("content")
                        "keywords" -> keywords = tag.attr("content")
                        "author" -> author = tag.attr("content")
                        "subtitle" -> subtitle = tag.attr("content")
                        "url" -> link = tag.attr("content")
                    }
                }

                // Process Open Graph meta tags
                ogTags.forEach { tag ->
                    val property = tag.attr("property")
                    when (property) {
                        "og:image" -> image = tag.attr("content")
                        "og:description" -> descriptionOg = tag.attr("content")
                        "og:url" -> linkOg = tag.attr("content")
                        "og:title" -> titleOg = tag.attr("content")
                    }
                }

                Result.success(
                    UrlMetadata(
                        title,
                        titleOg,
                        subtitle,
                        description,
                        descriptionOg,
                        icon,
                        image,
                        link,
                        linkOg,
                        keywords,
                        author
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}