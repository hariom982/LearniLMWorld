package com.example.learnilmworld.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class StudentScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : StudentScreen("home", "Home", Icons.Default.Home)
    object Dashboard : StudentScreen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Sessions : StudentScreen("sessions", "My Sessions", Icons.Default.CalendarToday)
    object Profile : StudentScreen("profile", "My Profile", Icons.Default.Person)
}

// language Card Data
data class LanguageCard(
    val flag: String,
    val language: String,
    val description: String,
    val backgroundColor: Color
)

data class CourseSlide(
    val title: String,
    val description: String,
    val backgroundColor: Color,
    val icon: String,
    val destination: String
)
data class ActionCard(
    val icon: String,
    val iconColor: Color,
    val title: String,
    val description: String,
    val buttonText: String,
    val buttonColor: Color,
    val destination: String
)

data class UpskillAddon(
    val icon: String,
    val title: String,
    val subtitle: String,
    val backgroundColor: Color
)
data class SubjectsCard(
    val icon: String,
    val title: String,
    val subtitle: String,
    val backgroundColor: Color
)

data class WisdomCard(
    val icon: String,
    val title: String,
    val subtitle: String,
    val iconColor: Color,
    val btntext: String
)

data class NewsUpdate(
    val image: String, // emoji or icon
    val title: String,
    val description: String,
    val date: String,
    val backgroundColor: Color
)