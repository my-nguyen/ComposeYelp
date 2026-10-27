package com.nguyen.composeyelp.data.remote

import com.nguyen.composeyelp.data.model.Business
import javax.inject.Inject
import javax.inject.Singleton

// turn YelpService and YelpRepository into classes passed in through constructors, with the
// ViewModel getting the repository the same way (manually or with Hilt), so you can swap in fakes.
@Singleton
class YelpService @Inject constructor(private val api: YelpApi) {
    suspend fun searchBusinesses(authorization: String, term: String, location: String): List<Business> {
        return api.searchBusinesses(authorization, term, location).businesses
    }
}
