package com.nguyen.composeyelp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage

@Composable
fun YelpScreen(modifier: Modifier = Modifier) {
    val viewModel: YelpViewModel = viewModel()
    val businesses = viewModel.businesses.value

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(businesses) {
            YelpBusiness(it)
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
            model = business.image_url,
            contentDescription = business.name,
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Color.LightGray),
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
                    text = "${business.review_count} reviews",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                text = business.location.address1 ?: "",
                fontSize = 14.sp,
            )
            Text(
                text = business.categories.firstOrNull()?.title ?: "",
                fontSize = 14.sp,
                color = Color.DarkGray,
            )
        }
        Column(
            modifier = Modifier.padding(start = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = business.toDistance(),
                fontSize = 14.sp,
                color = Color.DarkGray,
            )
            Text(
                text = business.price ?: "",
                fontSize = 14.sp,
                color = Color.DarkGray,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YelpBusinessScreen() {
    val business = Business("Deep Dish Pizza", 4.5, "$30", 389, 3.1, "", listOf(Category("Restaurant")), Location("101 Binary Rd"))
    MaterialTheme {
        YelpBusiness(business)
    }
}

@Composable
private fun RatingBar(rating: Double, modifier: Modifier = Modifier, stars: Int = 5) {
    Row(modifier = modifier) {
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
