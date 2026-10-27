package com.nguyen.composeyelp.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.nguyen.composeyelp.R
import com.nguyen.composeyelp.data.model.Business
import com.nguyen.composeyelp.data.model.Category
import com.nguyen.composeyelp.data.model.Location

@Composable
fun YelpScreen(
    modifier: Modifier = Modifier,
    viewModel: YelpViewModel = hiltViewModel()
) {
    var term by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier) {
        SearchBar(
            term = term,
            location = location,
            onTermChange = { term = it },
            onLocationChange = { location = it },
            onSearch = { viewModel.search(term, location) }
        )
        YelpContent(
            uiState = viewModel.uiState.value,
            onRetry = viewModel::retry,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SearchBar(
    term: String,
    location: String,
    onTermChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val canSearch = term.isNotBlank() && location.isNotBlank()
    val search = {
        if (canSearch) {
            keyboardController?.hide()
            onSearch()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = term,
            onValueChange = onTermChange,
            label = { Text("Search for") },
            placeholder = { Text("Pizza, coffee, tacos…") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Location") },
            placeholder = { Text("City, address or ZIP") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { search() }),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = search,
            enabled = canSearch,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Search")
        }
    }
}

@Composable
fun YelpContent(uiState: YelpUiState, modifier: Modifier = Modifier, onRetry: () -> Unit = {}) {
    // There's no loading or error state. The screen just stays empty until the data arrives, or for
    // good if the request fails. A small sealed state type (Loading / Success(list) / Error(message))
    // would let YelpScreen show a spinner or an error message.
    when (uiState) {
        is YelpUiState.Idle -> {
            Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "Enter what you're looking for and where to search.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        is YelpUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is YelpUiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Retry")
                }
            }
        }
        is YelpUiState.Success -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.businesses, key = { it.id }) {
                    YelpBusiness(it)
                }
            }
        }
    }
}

@Composable
fun YelpBusiness(business: Business, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = business.imageUrl,
            contentDescription = business.name,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = business.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingBar(rating = business.rating)
                Text(
                    text = "${business.reviewCount} reviews",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                text = business.location?.address1 ?: "",
                fontSize = 14.sp,
            )
            Text(
                text = business.categories?.firstOrNull()?.title ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = business.toDistance(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = business.price ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YelpBusinessScreen() {
    val business = Business("1", "Deep Dish Pizza", 4.5, "$30", 389, 3.1, "", listOf(Category("Restaurant")), Location("101 Binary Rd"))
    MaterialTheme {
        YelpBusiness(business)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(
            term = "Pizza",
            location = "San Francisco",
            onTermChange = {},
            onLocationChange = {},
            onSearch = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun YelpContentIdlePreview() {
    MaterialTheme {
        YelpContent(YelpUiState.Idle)
    }
}

@Preview(showBackground = true)
@Composable
fun YelpContentLoadingPreview() {
    MaterialTheme {
        YelpContent(YelpUiState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
fun YelpContentErrorPreview() {
    MaterialTheme {
        YelpContent(YelpUiState.Error("No internet connection. Check your connection and try again."))
    }
}

@Preview(showBackground = true)
@Composable
fun YelpContentSuccessPreview() {
    val businesses = listOf(
        Business("1", "Deep Dish Pizza", 4.5, "$30", 389, 3.1, "", listOf(Category("Restaurant")), Location("101 Binary Rd")),
        Business("2", "Slice House", 3.5, "$", 120, 1250.0, "", listOf(Category("Pizza")), Location("42 Market St"))
    )
    MaterialTheme {
        YelpContent(YelpUiState.Success(businesses))
    }
}

@Composable
private fun RatingBar(rating: Double, modifier: Modifier = Modifier, stars: Int = 5) {
    // Drop the ".0" so whole ratings read as "4 stars" rather than "4.0 stars"
    val ratingText = if (rating % 1.0 == 0.0) rating.toInt().toString() else rating.toString()
    val description = if (rating == 1.0) "1 star" else "$ratingText stars"
    Row(modifier = modifier.semantics { contentDescription = description }) {
        for (i in 1..stars) {
            val icon = when {
                rating >= i -> R.drawable.ic_star
                rating >= i - 0.5 -> R.drawable.ic_star_half
                else -> R.drawable.ic_star_border
            }
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
