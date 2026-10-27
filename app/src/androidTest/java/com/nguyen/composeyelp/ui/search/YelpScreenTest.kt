package com.nguyen.composeyelp.ui.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nguyen.composeyelp.data.model.Business
import com.nguyen.composeyelp.data.model.Category
import com.nguyen.composeyelp.data.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class YelpScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val business = Business(
        "1", "Deep Dish Pizza", 4.5, "$$", 389, 1000.0, null,
        listOf(Category("Pizza")), Location("101 Binary Rd")
    )

    @Test
    fun idleShowsPrompt() {
        composeRule.setContent { MaterialTheme { YelpContent(YelpUiState.Idle) } }

        composeRule.onNodeWithText("Enter what you're looking for and where to search.").assertIsDisplayed()
    }

    @Test
    fun errorShowsMessageAndRetryCallsBack() {
        var retried = false
        composeRule.setContent {
            MaterialTheme { YelpContent(YelpUiState.Error("No internet connection."), onRetry = { retried = true }) }
        }

        composeRule.onNodeWithText("No internet connection.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()
        assertTrue(retried)
    }

    @Test
    fun successShowsBusinessDetails() {
        composeRule.setContent { MaterialTheme { YelpContent(YelpUiState.Success(listOf(business))) } }

        composeRule.onNodeWithText("Deep Dish Pizza").assertIsDisplayed()
        composeRule.onNodeWithText("389 reviews").assertIsDisplayed()
        composeRule.onNodeWithText("101 Binary Rd").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("4.5 stars").assertIsDisplayed()
    }

    @Test
    fun searchButtonDisabledUntilBothFieldsFilled() {
        composeRule.setContent {
            MaterialTheme { SearchBar("Pizza", "", onTermChange = {}, onLocationChange = {}, onSearch = {}) }
        }

        composeRule.onNodeWithText("Search").assertIsNotEnabled()
    }

    @Test
    fun searchButtonCallsBackWhenFieldsFilled() {
        var searches = 0
        composeRule.setContent {
            MaterialTheme {
                SearchBar("Pizza", "Austin", onTermChange = {}, onLocationChange = {}, onSearch = { searches++ })
            }
        }

        composeRule.onNodeWithText("Search").assertIsEnabled().performClick()
        assertEquals(1, searches)
    }
}
