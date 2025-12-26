package com.example.learnilmworld.trainer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.learnilmworld.R
import com.example.learnilmworld.screen.TrainerScreen
import com.example.learnilmworld.screen.TrainerStatsCard
import com.example.learnilmworld.viewModel.AuthViewModel

@Composable
fun TrainerDashboardScreen(viewModel: AuthViewModel,navController: NavHostController) {
    val currentUser by viewModel.currentUser.collectAsState()

    val statsCards = listOf(
        TrainerStatsCard(
            icon = "📖",
            iconColor = Color(0xFF8B7FD8),
            value = "0",
            label = "Total Sessions",
            labelColor = Color(0xFF8B7FD8)
        ),
        TrainerStatsCard(
            icon = "📅",
            iconColor = Color(0xFF10B981),
            value = "0",
            label = "Upcoming",
            labelColor = Color(0xFF10B981)
        ),
        TrainerStatsCard(
            icon = "⭐",
            iconColor = Color(0xFFFF8C42),
            value = "0",
            label = "Completed",
            labelColor = Color(0xFFFF8C42)
        ),
        TrainerStatsCard(
            icon = "💰",
            iconColor = Color(0xFF10B981),
            value = "$0",
            label = "Total Earnings",
            labelColor = Color(0xFF10B981)
        ),
        TrainerStatsCard(
            icon = "👥",
            iconColor = Color(0xFF3B82F6),
            value = "0",
            label = "Students",
            labelColor = Color(0xFF3B82F6)
        ),
        TrainerStatsCard(
            icon = "🏆",
            iconColor = Color(0xFFFFB020),
            value = "5.0",
            label = "Rating",
            labelColor = Color(0xFFFFB020)
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
            // Welcome Header
            item {
                WelcomeHeader(trainerName = currentUser?.fullName ?: " ")
            }

            // Stats Cards Grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(320.dp),
                    userScrollEnabled = false
                ) {
                    items(statsCards) { stats ->
                        TrainerStatsCardItem(stats = stats)
                    }
                }
            }

            // Recent Bookings Section
            item {
                RecentBookingsSection(navController)
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeHeader(trainerName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF5353CE).copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(R.drawable.baseline_trending_up_24),
                    contentDescription = "Wave",
                    colorFilter = ColorFilter.tint(Color(0xFF5353CE)),
                    modifier = Modifier.height(20.dp))            }

            Column {
                Text(
                    text = "Welcome back, $trainerName!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Empower your students and grow your teaching journey",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerStatsCardItem(stats: TrainerStatsCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(stats.iconColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stats.icon, fontSize = 24.sp)
            }

            Text(
                text = stats.value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D2D44)
            )

            Text(
                text = stats.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = stats.labelColor,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentBookingsSection(navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Bookings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44)
                )

                TextButton(onClick = { /* Navigate to all students */ }) {
                    Text(
                        text = "View All \nStudents",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8B7FD8)
                    )

                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF8B7FD8).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.group),
                        contentDescription = "No sessions image",
                        modifier = Modifier.height(30.dp)
                    )
                }

                Text(
                    text = "No bookings yet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44)
                )

                Text(
                    text = "You'll see bookings here once students start enrolling",
                    fontSize = 15.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.navigate(TrainerScreen.Sessions.route)},
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C42)),
                    shape = RoundedCornerShape(14.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    )
                ) {
                    Text(
                        text = "View Your Sessions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}