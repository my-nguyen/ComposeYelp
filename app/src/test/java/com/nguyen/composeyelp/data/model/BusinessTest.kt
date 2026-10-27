package com.nguyen.composeyelp.data.model

import com.nguyen.composeyelp.data.remote.business
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class BusinessTest {
    private val defaultLocale = Locale.getDefault()

    @Before
    fun setUp() {
        // toDistance() formats with the default locale, so pin it for predictable output
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun toDistanceConvertsMetersToMiles() {
        assertEquals("2.00 mi", business("1").copy(distance = 3218.688).toDistance())
    }

    @Test
    fun toDistanceRoundsToTwoDecimals() {
        assertEquals("0.62 mi", business("1").copy(distance = 1000.0).toDistance())
    }
}
