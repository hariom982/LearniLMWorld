package com.example.learnilmworld.screen

sealed class Screen(val route: String) {
    object choiceScreen : Screen("authentication")
    object StudentSignup : Screen("student_signup")
    object TrainerSignup : Screen("trainer_signup")
}
