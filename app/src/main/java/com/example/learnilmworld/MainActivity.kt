package com.example.learnilmworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learnilmworld.screen.Screen
import com.example.learnilmworld.student.BrowseTrainersScreen
import com.example.learnilmworld.student.StudentMainScreen
import com.example.learnilmworld.student.StudentSignupScreen
import com.example.learnilmworld.trainer.TrainerMainScreen
import com.example.learnilmworld.trainer.TrainerSignupScreen
import com.example.learnilmworld.viewModel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val viewModel: AuthViewModel = viewModel()
        val authState by viewModel.authState.collectAsState()
        val currentUser by viewModel.currentUser.collectAsState()
        // Auto-navigate on app start if user is logged in

        LaunchedEffect(currentUser) {
            currentUser?.let { user ->
                val route = if (user.userType == "STUDENT") {
                    "student_home"
                } else {
                    "trainer_home"
                }
                navController.navigate(route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = "onboarding"
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate("choicescreen") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("choicescreen") {
                choiceScreen(navController)
            }
            composable("signin") {
                SigninScreen(navController,viewModel)
            }
            composable(Screen.StudentSignup.route) {
                StudentSignupScreen(navController,viewModel)
            }
            composable("student_home") {
                StudentMainScreen(viewModel,navController)
            }
            composable("trainer_home") {
                TrainerMainScreen(viewModel,navController)
            }
            composable(Screen.TrainerSignup.route) {
                TrainerSignupScreen(navController,viewModel)
            }
            composable("browse_trainers"){
                BrowseTrainersScreen(navController)
            }
        }
    }
}