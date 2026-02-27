package com.example.learnilmworld.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
                StudentHomeScreen(viewModel,navController)
            }
            composable(StudentScreen.Dashboard.route) {
                DashboardScreen(navController)
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

//    NavigationBar(
//        modifier = Modifier
//            .fillMaxWidth()
//            .windowInsetsPadding(WindowInsets.navigationBars),
//        containerColor = Color.White,
//    ) {
//        items.forEach { screen ->
//            val isSelected = currentRoute == screen.route
//
//            NavigationBarItem(
//                selected = isSelected,
//                onClick = {
//                    navController.navigate(screen.route) {
//                        popUpTo(StudentScreen.Home.route) { saveState = true }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                },
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
//                        fontSize = 10.sp,
//                    )
//                },
//                alwaysShowLabel = true,
//                modifier = Modifier.padding(vertical = 1.dp),
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
                                popUpTo(StudentScreen.Home.route) { saveState = true }
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