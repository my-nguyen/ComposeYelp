package com.nguyen.composeyelp.data.remote

import com.nguyen.composeyelp.data.model.Business
import com.nguyen.composeyelp.data.model.YelpResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

// Using object singletons makes testing hard. That's fine for an app this size. If you add tests
// later, turn YelpService and YelpRepository into classes passed in through constructors, with the
// ViewModel getting the repository the same way (manually or with Hilt), so you can swap in fakes.
@Singleton
class YelpService @Inject constructor(private val service: Service) {
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