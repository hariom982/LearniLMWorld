package com.example.learnilmworld.student

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.learnilmworld.screen.StudentScreen
import com.example.learnilmworld.viewModel.AuthViewModel

@Composable
fun StudentMainScreen(viewModel: AuthViewModel,
                      navController: NavHostController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            StudentBottomNavigationBar(navController = bottomNavController)
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = StudentScreen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(StudentScreen.Home.route) {
                StudentHomeScreen(navController)
            }
            composable(StudentScreen.Dashboard.route) {
                DashboardScreen()
            }
            composable(StudentScreen.Sessions.route) {
                SessionsScreen()
            }
            composable(StudentScreen.Profile.route) {
                ProfileScreen(viewModel = viewModel, navController = navController)
            }
        }
    }
}

@Composable
fun StudentBottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        StudentScreen.Home,
        StudentScreen.Dashboard,
        StudentScreen.Sessions,
        StudentScreen.Profile
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
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(StudentScreen.Home.route) {
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