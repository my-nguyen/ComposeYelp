package com.nguyen.composeyelp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

object YelpService {
    private var service: Service
    private const val BASE_URL = "https://api.yelp.com/v3/"

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(Service::class.java)
    }

    suspend fun searchBusinesses(authorization: String, term: String, location: String): List<Business> {
        return service.searchBusinesses(authorization, term, location).businesses
    }

    interface Service {
        @GET("businesses/search")
        suspend fun searchBusinesses(
            @Header("Authorization") auth: String,
            @Query("term") term: String,
            @Query("location") location: String
        ): YelpResult
    }
}