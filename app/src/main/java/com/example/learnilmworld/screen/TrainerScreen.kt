package com.example.learnilmworld.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TrainerScreen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : TrainerScreen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Sessions : TrainerScreen("sessions", "My Sessions", Icons.Default.CalendarToday)
    object Students : TrainerScreen("students", "Students", Icons.Default.Group)
    object Reviews : TrainerScreen("reviews", "Reviews", Icons.Default.Star)
    object Profile : TrainerScreen("profile", "Profile", Icons.Default.Person)
}

// Trainer Stats Card Data
data class TrainerStatsCard(
    val icon: String,
    val iconColor: Color,
    val value: String,
    val label: String,
    val labelColor: Color
)