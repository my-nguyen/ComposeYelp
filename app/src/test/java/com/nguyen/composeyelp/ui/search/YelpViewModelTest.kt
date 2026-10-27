package com.nguyen.composeyelp.ui.search

import com.nguyen.composeyelp.data.model.YelpResult
import com.nguyen.composeyelp.data.remote.FakeYelpApi
import com.nguyen.composeyelp.data.remote.YelpService
import com.nguyen.composeyelp.data.remote.business
import com.nguyen.composeyelp.data.repository.YelpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

@OptIn(ExperimentalCoroutinesApi::class)
class YelpViewModelTest {
    private val api = FakeYelpApi()
    private lateinit var viewModel: YelpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = YelpViewModel(YelpRepository(YelpService(api)))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsIdleWithoutSearching() {
        assertEquals(YelpUiState.Idle, viewModel.uiState.value)
        assertEquals(0, api.requests.size)
    }

    @Test
    fun searchShowsLoadingThenSuccess() = runTest {
        val businesses = listOf(business("1"), business("2"))
        api.response = { YelpResult(2, businesses) }

        viewModel.search("Pizza", "San Francisco")
        assertEquals(YelpUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()
        assertEquals(YelpUiState.Success(businesses), viewModel.uiState.value)
    }

    @Test
    fun searchTrimsTermAndLocation() = runTest {
        viewModel.search("  Pizza ", " San Francisco  ")
        advanceUntilIdle()

        assertEquals("Pizza", api.requests.single().term)
        assertEquals("San Francisco", api.requests.single().location)
    }

    @Test
    fun retryRepeatsLastSearch() = runTest {
        api.response = { throw IOException() }
        viewModel.search("Tacos", "Austin")
        advanceUntilIdle()

        api.response = { YelpResult(1, listOf(business("1"))) }
        viewModel.retry()
        advanceUntilIdle()

        assertEquals(2, api.requests.size)
        assertEquals("Tacos", api.requests.last().term)
        assertEquals("Austin", api.requests.last().location)
        assertEquals(YelpUiState.Success(listOf(business("1"))), viewModel.uiState.value)
    }

    @Test
    fun newSearchReplacesSlowerEarlierSearch() = runTest {
        api.response = { term ->
            if (term == "slow") delay(1_000)
            YelpResult(1, listOf(business(term)))
        }

        viewModel.search("slow", "Austin")
        viewModel.search("fast", "Austin")
        advanceUntilIdle()

        assertEquals(YelpUiState.Success(listOf(business("fast"))), viewModel.uiState.value)
    }

    @Test
    fun timeoutShowsTimeoutMessage() = runTest {
        assertErrorMessage(SocketTimeoutException(), "The request timed out. Please try again.")
    }

    @Test
    fun networkErrorShowsNoConnectionMessage() = runTest {
        assertErrorMessage(IOException(), "No internet connection. Check your connection and try again.")
    }

    @Test
    fun unauthorizedShowsApiKeyMessage() = runTest {
        assertErrorMessage(httpException(401), "Couldn't connect to Yelp. Check your API key.")
        assertErrorMessage(httpException(403), "Couldn't connect to Yelp. Check your API key.")
    }

    @Test
    fun rateLimitedShowsTooManyRequestsMessage() = runTest {
        assertErrorMessage(httpException(429), "Too many requests. Please wait a moment and try again.")
    }

    @Test
    fun serverErrorShowsYelpTroubleMessage() = runTest {
        assertErrorMessage(httpException(503), "Yelp is having trouble right now. Please try again later.")
    }

    @Test
    fun otherHttpErrorShowsGenericLoadMessage() = runTest {
        assertErrorMessage(httpException(404), "Couldn't load businesses. Please try again.")
    }

    @Test
    fun unexpectedErrorShowsGenericMessage() = runTest {
        assertErrorMessage(IllegalStateException(), "Something went wrong. Please try again.")
    }

    private fun kotlinx.coroutines.test.TestScope.assertErrorMessage(exception: Exception, expected: String) {
        api.response = { throw exception }
        viewModel.search("Pizza", "San Francisco")
        advanceUntilIdle()
        assertEquals(YelpUiState.Error(expected), viewModel.uiState.value)
    }

    private fun httpException(code: Int) =
        HttpException(Response.error<YelpResult>(code, "".toResponseBody()))
}
