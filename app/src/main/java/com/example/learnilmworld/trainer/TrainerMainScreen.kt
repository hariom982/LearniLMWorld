package com.example.learnilmworld.trainer

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.learnilmworld.screen.TrainerScreen
import com.example.learnilmworld.viewModel.AuthViewModel

@Composable
fun TrainerMainScreen(viewModel: AuthViewModel,
                      navController: NavHostController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            TrainerBottomNavigationBar(navController = bottomNavController)
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = TrainerScreen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(TrainerScreen.Dashboard.route) {
                TrainerDashboardScreen(viewModel)
            }
            composable(TrainerScreen.Sessions.route) {
                TrainerSessionsScreen()
            }
            composable(TrainerScreen.Students.route) {
                TrainerStudentsScreen()
            }
            composable(TrainerScreen.Reviews.route) {
                TrainerReviewsScreen()
            }
            composable(TrainerScreen.Profile.route) {
                TrainerProfileScreen(viewModel,navController)
            }
        }
    }
}

@Composable
fun TrainerBottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        TrainerScreen.Dashboard,
        TrainerScreen.Sessions,
        TrainerScreen.Students,
        TrainerScreen.Reviews,
        TrainerScreen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF2D2D44),
        contentColor = Color.White,
        modifier = Modifier.height(70.dp)
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(TrainerScreen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFB8E986),
                    selectedTextColor = Color(0xFFB8E986),
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}