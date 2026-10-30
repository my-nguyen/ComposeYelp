package com.nguyen.composeyelp.data.repository

import com.nguyen.composeyelp.BuildConfig
import com.nguyen.composeyelp.data.model.Business
import com.nguyen.composeyelp.data.remote.YelpService

class YelpRepository(private val yelpService: YelpService) {
    suspend fun searchBusinesses(term: String, location: String): List<Business> {
        return yelpService.searchBusinesses(AUTHORIZATION, term, location)
    }

    companion object {
        private const val AUTHORIZATION = "Bearer ${BuildConfig.YELP_API_KEY}"
    }
}
