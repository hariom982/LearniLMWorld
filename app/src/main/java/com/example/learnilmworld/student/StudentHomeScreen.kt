package com.example.learnilmworld.student

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnilmworld.screen.ActionCard
import com.example.learnilmworld.screen.ServiceCard
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentHomeScreen() {
    val services = listOf(
        ServiceCard(
            icon = "📖",
            iconColor = Color(0xFF10B981),
            title = "English Training",
            description = "Improve communication and language skills"
        ),
        ServiceCard(
            icon = "👥",
            iconColor = Color(0xFF3B82F6),
            title = "Professional Trainers",
            description = "Get expert guidance for your career path"
        ),
        ServiceCard(
            icon = "💬",
            iconColor = Color(0xFFA855F7),
            title = "Improve Communication",
            description = "Practice and perfect your interview skills"
        ),
        ServiceCard(
            icon = "⭐",
            iconColor = Color(0xFFFF6B35),
            title = "Language Development",
            description = "Build confidence and interpersonal skills"
        )
    )

    val actionCards = listOf(
        ActionCard(
            icon = "👥",
            iconColor = Color(0xFF10B981),
            title = "Find Your Perfect Trainer",
            description = "Browse through our network of expert trainers and counselors to find the perfect match for your learning goals.",
            buttonText = "Browse All Trainers",
            buttonColor = Color(0xFF10B981)
        ),
        ActionCard(
            icon = "💬",
            iconColor = Color(0xFF3B82F6),
            title = "Your Learning Journey",
            description = "Keep track of your sessions, view feedback from trainers, and monitor your progress.",
            buttonText = "View My Sessions",
            buttonColor = Color(0xFF3B82F6)
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Explore Services",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D2D44),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Service Cards Grid
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(450.dp),
                    userScrollEnabled = false
                ) {
                    items(services) { service ->
                        ServiceCardItem(service = service)
                    }
                }
            }

            // Action Cards
            items(actionCards) { actionCard ->
                ActionCardItem(actionCard = actionCard)
            }

        }
    }
}

@Composable
fun ServiceCardItem(service: ServiceCard) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .scale(scale)
            .clickable {
                isPressed = !isPressed
                // Handle card click action here if required
            },
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, RoundedCornerShape(14.dp))
                    .background(color = service.iconColor, shape =  RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = service.icon,
                    fontSize = 28.sp
                )
            }

            // Title and Description
            Column {
                Text(
                    text = service.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = service.description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ActionCardItem(actionCard: ActionCard) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .shadow(8.dp, RoundedCornerShape(14.dp))
                    .background(actionCard.iconColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionCard.icon,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = actionCard.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D44)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = actionCard.description,
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(
                onClick = { /* Handle action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = actionCard.buttonColor
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 10.dp
                )
            ) {
                Text(
                    text = actionCard.buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
