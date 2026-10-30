package com.nguyen.composeyelp.ui.search

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nguyen.composeyelp.data.model.Business
import com.nguyen.composeyelp.data.remote.RetrofitClient
import com.nguyen.composeyelp.data.remote.YelpService
import com.nguyen.composeyelp.data.repository.YelpRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface YelpUiState {
    data object Idle : YelpUiState
    data object Loading : YelpUiState
    data class Success(val businesses: List<Business>) : YelpUiState
    data class Error(val message: String) : YelpUiState
}

class YelpViewModel(private val repository: YelpRepository) : ViewModel() {
    private val _uiState = mutableStateOf<YelpUiState>(YelpUiState.Idle)
    val uiState: State<YelpUiState> = _uiState

    private var searchJob: Job? = null
    private var lastTerm = ""
    private var lastLocation = ""

    fun search(term: String, location: String) {
        lastTerm = term.trim()
        lastLocation = location.trim()
        loadBusinesses()
    }

    fun retry() {
        loadBusinesses()
    }

    private fun loadBusinesses() {
        // Cancel any search still running so a slow older response can't overwrite a newer one
        searchJob?.cancel()
        _uiState.value = YelpUiState.Loading
        searchJob = viewModelScope.launch {
            try {
                val businesses = repository.searchBusinesses(lastTerm, lastLocation)
                _uiState.value = YelpUiState.Success(businesses)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to search businesses", e)
                _uiState.value = YelpUiState.Error(e.toErrorMessage())
            }
        }
    }

    private fun Exception.toErrorMessage(): String = when (this) {
        is SocketTimeoutException -> "The request timed out. Please try again."
        is IOException -> "No internet connection. Check your connection and try again."
        is HttpException -> when (code()) {
            401, 403 -> "Couldn't connect to Yelp. Check your API key."
            429 -> "Too many requests. Please wait a moment and try again."
            in 500..599 -> "Yelp is having trouble right now. Please try again later."
            else -> "Couldn't load businesses. Please try again."
        }
        else -> "Something went wrong. Please try again."
    }

    companion object {
        private const val TAG = "YelpViewModel"

        val Factory = viewModelFactory {
            initializer { YelpViewModel(YelpRepository(YelpService(RetrofitClient.yelpApi))) }
        }
    }
}
