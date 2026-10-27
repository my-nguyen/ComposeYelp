package com.nguyen.composeyelp.data.repository

import com.nguyen.composeyelp.BuildConfig
import com.nguyen.composeyelp.data.model.Business
import com.nguyen.composeyelp.data.remote.YelpService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YelpRepository @Inject constructor(private val yelpService: YelpService) {
    suspend fun searchBusinesses(term: String, location: String): List<Business> {
        return yelpService.searchBusinesses(AUTHORIZATION, term, location)
    }

    companion object {
        private const val AUTHORIZATION = "Bearer ${BuildConfig.YELP_API_KEY}"
    }
}
