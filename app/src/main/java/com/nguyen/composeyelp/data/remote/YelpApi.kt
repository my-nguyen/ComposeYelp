package com.nguyen.composeyelp.data.remote

import com.nguyen.composeyelp.data.model.YelpResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YelpApi {
    @GET("businesses/search")
    suspend fun searchBusinesses(
        @Header("Authorization") auth: String,
        @Query("term") term: String,
        @Query("location") location: String
    ): YelpResult
}
