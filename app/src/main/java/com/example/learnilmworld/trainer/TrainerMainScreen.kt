package com.example.learnilmworld.trainer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
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
                TrainerDashboardScreen(viewModel,bottomNavController)
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

//    NavigationBar(
//        modifier = Modifier
//            .height(70.dp)
//            .windowInsetsPadding(
//                WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
//            ),
//        containerColor = Color.White,
//
//        contentColor = Color.Gray,
//    ) {
//        items.forEach { screen ->
//            val isSelected = currentRoute == screen.route
//
//            NavigationBarItem(
//                modifier = Modifier.padding(top = 2.dp),
//                icon = {
//                    Icon(
//                        imageVector = screen.icon,
//                        contentDescription = screen.title,
//                        modifier = Modifier.size(18.dp)
//                    )
//                },
//                label = {
//                    Text(
//                        text = screen.title,
//                        fontSize = 11.sp,
//                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
//                    )
//                },
//                selected = isSelected,
//                onClick = {
//                    navController.navigate(screen.route) {
//                        popUpTo(TrainerScreen.Dashboard.route) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                },
//                colors = NavigationBarItemDefaults.colors(
//                    selectedIconColor = Color(0xFF0D7611),
//                    selectedTextColor = Color(0xFF0D7611),
//                    unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
//                    unselectedTextColor = Color.Gray.copy(alpha = 0.6f),
//                    indicatorColor = Color.Transparent
//                )
//            )
//        }
//    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentRoute == screen.route

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(screen.route) {
                                popUpTo(TrainerScreen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(18.dp),
                        tint = if (isSelected)
                            Color(0xFF0D7611)
                        else
                            Color.Gray.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = screen.title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected)
                            Color(0xFF0D7611)
                        else
                            Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }

}