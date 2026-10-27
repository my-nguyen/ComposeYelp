package com.nguyen.composeyelp.data.repository

import com.nguyen.composeyelp.BuildConfig
import com.nguyen.composeyelp.data.model.YelpResult
import com.nguyen.composeyelp.data.remote.FakeYelpApi
import com.nguyen.composeyelp.data.remote.YelpService
import com.nguyen.composeyelp.data.remote.business
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class YelpRepositoryTest {
    private val api = FakeYelpApi()
    private val repository = YelpRepository(YelpService(api))

    @Test
    fun searchSendsBearerAuthorizationHeader() = runTest {
        repository.searchBusinesses("Pizza", "San Francisco")

        assertEquals("Bearer ${BuildConfig.YELP_API_KEY}", api.requests.single().auth)
    }

    @Test
    fun searchReturnsBusinessesFromResult() = runTest {
        val businesses = listOf(business("1"), business("2"))
        api.response = { YelpResult(2, businesses) }

        assertEquals(businesses, repository.searchBusinesses("Pizza", "San Francisco"))
    }
}
