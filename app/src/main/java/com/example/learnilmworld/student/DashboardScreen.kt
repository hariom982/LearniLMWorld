package com.example.learnilmworld.student

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun DashboardScreen() {
    val statsCards = listOf(
        StatsCard(
            icon = "📖",
            iconColor = Color(0xFF8B7FD8),
            value = "0",
            label = "Total Sessions",
            labelColor = Color(0xFF8B7FD8)
        ),
        StatsCard(
            icon = "📅",
            iconColor = Color(0xFF5FD4A8),
            value = "0",
            label = "Upcoming",
            labelColor = Color(0xFF10B981)
        ),
        StatsCard(
            icon = "⭐",
            iconColor = Color(0xFFFF8C42),
            value = "0",
            label = "Completed",
            labelColor = Color(0xFFFF6B35)
        ),
        StatsCard(
            icon = "🌐",
            iconColor = Color(0xFF8B7FD8),
            value = "$0",
            label = "Total Spent",
            labelColor = Color(0xFF8B7FD8)
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
            // Stats Cards Grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(320.dp),
                    userScrollEnabled = false
                ) {
                    items(statsCards) { stats ->
                        StatsCardItem(stats = stats)
                    }
                }
            }

            // Recent Sessions Section
            item {
                RecentSessionsSection()
            }
        }
    }
}

data class StatsCard(
    val icon: String,
    val iconColor: Color,
    val value: String,
    val label: String,
    val labelColor: Color
)

@Composable
fun StatsCardItem(stats: StatsCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
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
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(stats.iconColor.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stats.icon,
                    fontSize = 28.sp
                )
            }

            // Value
            Text(
                text = stats.value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D2D44)
            )

            // Label
            Text(
                text = stats.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = stats.labelColor
            )
        }
    }
}

@Composable
fun RecentSessionsSection() {
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
                .padding(28.dp)
        ) {
            // Header with "View All" button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Sessions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44)
                )

                TextButton(onClick = { /* Navigate to sessions */ }) {
                    Text(
                        text = "View All",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8B7FD8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF8B7FD8).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📖",
                        fontSize = 40.sp
                    )
                }

                // Title
                Text(
                    text = "No sessions yet",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44)
                )

                // Description
                Text(
                    text = "Start your learning journey today",
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Book Session Button
                Button(
                    onClick = { /* Navigate to book session */ },
                    modifier = Modifier
                        .padding(horizontal = 25.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF8C42)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    )
                ) {
                    Text(
                        text = "Book Your First Session",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}