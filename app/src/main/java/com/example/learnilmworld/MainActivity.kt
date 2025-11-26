package com.example.learnilmworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learnilmworld.screen.Screen
import com.example.learnilmworld.student.StudentMainScreen
import com.example.learnilmworld.student.StudentSignupScreen
import com.example.learnilmworld.trainer.TrainerMainScreen
import com.example.learnilmworld.trainer.TrainerSignupScreen

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
                SigninScreen(navController)
            }
            composable(Screen.StudentSignup.route) {
                StudentSignupScreen(navController)
            }
            composable("student_home") {
                StudentMainScreen()
            }
            composable("trainer_home") {
                TrainerMainScreen(trainerName = "Hariom Rajak")
            }
            composable(Screen.TrainerSignup.route) {
                TrainerSignupScreen(navController)
            }
        }
    }
}