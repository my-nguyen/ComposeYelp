package com.nguyen.composeyelp.data.remote

import com.nguyen.composeyelp.data.model.Business
import com.nguyen.composeyelp.data.model.Category
import com.nguyen.composeyelp.data.model.Location
import com.nguyen.composeyelp.data.model.YelpResult

class FakeYelpApi : YelpService.Service {
    data class Request(val auth: String, val term: String, val location: String)

    val requests = mutableListOf<Request>()
    var response: suspend (term: String) -> YelpResult = { YelpResult(0, emptyList()) }

    override suspend fun searchBusinesses(auth: String, term: String, location: String): YelpResult {
        requests += Request(auth, term, location)
        return response(term)
    }
}

fun business(id: String, name: String = "Business $id") =
    Business(id, name, 4.5, "$$", 100, 1000.0, null, listOf(Category("Pizza")), Location("1 Main St"))
