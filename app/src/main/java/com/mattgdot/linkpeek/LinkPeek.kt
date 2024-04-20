package com.mattgdot.linkpeek

import com.mattgdot.linkpeek.models.UrlMetadata
import com.mattgdot.linkpeek.utils.MetadataFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LinkPeek(
    private val url:String
) {
    fun peekLink(
        onMetadataLoaded:(Result<UrlMetadata>)->Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch{
            MetadataFetcher.fetchMetadata(url).let {
                onMetadataLoaded(it)
            }
        }
    }
}