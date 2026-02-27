package com.example.learnilmworld

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
//import com.example.learnilmworld.retrofit.mongo_backend.TempLoginScreen
import com.example.learnilmworld.screen.Screen
import com.example.learnilmworld.student.BrowseTrainersScreen
import com.example.learnilmworld.student.StudentMainScreen
import com.example.learnilmworld.student.StudentOnboardingScreen
import com.example.learnilmworld.student.StudentSignupScreen
import com.example.learnilmworld.trainer.TrainerMainScreen
import com.example.learnilmworld.trainer.TrainerSignupScreen
import com.example.learnilmworld.viewModel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionsIfNeeded()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim =android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT,
            )
        )
        setContent {
            AppNavigation()
//            val navController = rememberNavController()
//            TempLoginScreen(navController)
        }
    }
    private val PERMISSION_REQ_ID = 22
    private val REQUIRED_PERMISSIONS = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.CAMERA
    )

    private fun checkPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissionsIfNeeded() {
        if (!checkPermissions()) {
            requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_REQ_ID)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults.all { it == android.content.pm.PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera & Mic permissions required", Toast.LENGTH_LONG).show()
                finish()
            }
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
//            composable("browse_trainers"){
//                BrowseTrainersScreen(navController)
//            }
            composable(
                route = "browse_trainers/{language}",
                arguments = listOf(navArgument("language") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { backStackEntry ->
                BrowseTrainersScreen(
                    navController = navController,
                    selectedLanguage = backStackEntry.arguments?.getString("language")
                )
            }
            // Add this to your NavHost in MainActivity.kt

            composable("student_onboarding/{email}") { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                StudentOnboardingScreen(
                    navController = navController,
                    studentEmail = email
                )
            }
        }
    }
}