package com.nguyen.composeyelp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YelpViewModel : ViewModel() {
    val businesses = mutableStateOf(listOf<Business>())

    init {
        viewModelScope.launch {
            businesses.value = YelpRepository.searchBusinesses("Bearer ${BuildConfig.YELP_API_KEY}", "Pizza", "San Francisco")
        }
    }
}