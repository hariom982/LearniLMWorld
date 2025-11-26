package com.example.learnilmworld.trainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrainerReviewsScreen() {
    val averageRating = 4.8f
    val totalReviews = 24
    val ratingDistribution = mapOf(
        5 to 18,
        4 to 4,
        3 to 1,
        2 to 1,
        1 to 0
    )

    // Sample reviews data
    val reviews = listOf(
        ReviewItem(
            studentName = "Sarah Johnson",
            rating = 5,
            date = "2 days ago",
            comment = "Excellent teacher! Very patient and explains concepts clearly. My English has improved significantly.",
            avatar = "👩"
        ),
        ReviewItem(
            studentName = "Michael Chen",
            rating = 5,
            date = "1 week ago",
            comment = "Best language tutor I've had. The sessions are engaging and I feel more confident speaking now.",
            avatar = "👨"
        ),
        ReviewItem(
            studentName = "Emma Rodriguez",
            rating = 4,
            date = "2 weeks ago",
            comment = "Great experience overall. Would recommend to anyone looking to improve their language skills.",
            avatar = "👧"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD4A574),
                        Color(0xFFE6B87D)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header with stats
            item {
                ReviewsHeader(
                    averageRating = averageRating,
                    totalReviews = totalReviews,
                    distribution = ratingDistribution
                )
            }

            // Reviews List Title
            item {
                Text(
                    text = "Student Reviews ($totalReviews)",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Reviews
            if (reviews.isEmpty()) {
                item {
                    EmptyReviewsCard()
                }
            } else {
                items(reviews.size) { index ->
                    ReviewCard(review = reviews[index])
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

data class ReviewItem(
    val studentName: String,
    val rating: Int,
    val date: String,
    val comment: String,
    val avatar: String
)

@Composable
fun ReviewsHeader(
    averageRating: Float,
    totalReviews: Int,
    distribution: Map<Int, Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Left side - Average Rating
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%.1f", averageRating),
                    fontSize = 50.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D2D44)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < averageRating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index < averageRating.toInt()) Color(0xFFFFB020) else Color(0xFFD1D5DB),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Based on $totalReviews reviews",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(150.dp)
                    .background(Color(0xFFE5E7EB))
            )

            // Right side - Distribution
            Column(
                modifier = Modifier.weight(1.5f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val total = distribution.values.sum()

                (5 downTo 1).forEach { stars ->
                    CompactRatingBar(
                        stars = stars,
                        count = distribution[stars] ?: 0,
                        total = total
                    )
                }
            }
        }
    }
}

@Composable
fun CompactRatingBar(
    stars: Int,
    count: Int,
    total: Int
) {
    val percentage = if (total > 0) (count.toFloat() / total.toFloat()) else 0f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            repeat(stars) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB020),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Progress bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .background(Color(0xFFFF8C42), RoundedCornerShape(4.dp))
            )
        }

        // Count
        Text(
            text = "$count",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D44),
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ReviewCard(review: ReviewItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Student info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF8C42),
                                        Color(0xFFFFB89D)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.avatar,
                            fontSize = 24.sp
                        )
                    }

                    Column {
                        Text(
                            text = review.studentName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D44)
                        )

                        Text(
                            text = review.date,
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }

                // Rating stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(review.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB020),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Review comment
            Text(
                text = review.comment,
                fontSize = 15.sp,
                color = Color(0xFF4B5563),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun EmptyReviewsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFFF8C42).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = Color(0xFFFF8C42),
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "No reviews yet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D44)
            )

            Text(
                text = "Your student reviews will appear here after they complete sessions and leave feedback.",
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}