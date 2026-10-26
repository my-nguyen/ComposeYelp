package com.nguyen.composeyelp


object YelpRepository {
    private var businesses = listOf<Business>()

    suspend fun searchBusinesses(authorization: String, term: String, location: String): List<Business> {
        businesses = YelpService.searchBusinesses(authorization, term, location)
        return businesses
    }
}