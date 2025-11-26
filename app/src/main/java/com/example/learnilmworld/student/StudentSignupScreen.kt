package com.example.learnilmworld.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.learnilmworld.trainer.ExperienceSelector
import com.example.learnilmworld.trainer.FormTextField
import com.example.learnilmworld.trainer.LanguageChipSelector
import com.example.learnilmworld.trainer.SignupHeader
import com.example.learnilmworld.trainer.SubmitButton

@Composable
fun StudentSignupScreen(navController: NavHostController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneno by remember { mutableStateOf("") }
    var nativeLanguage by remember { mutableStateOf("") }
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }
    var learningLevel by remember { mutableStateOf("") }

    val languages = listOf("Spanish", "French", "German", "Italian", "Japanese", "Korean", "Mandarin", "Portuguese")
    val nativeLanguageOptions = listOf("English", "Spanish", "French", "German", "Hindi", "Mandarin", "Japanese")


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFFf093fb)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SignupHeader(
                emoji = "🎓",
                title = "Student Sign Up",
                subtitle = "Start your language learning journey"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "First Name",
                    placeholder = "First name",
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Last Name",
                    placeholder = "Last name",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = password,
                onValueChange = { password = it },
                label = "Confirm Password",
                placeholder = "Confirm password",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = phoneno,
                onValueChange = {phoneno = it },
                label = "Mobile Number",
                placeholder = "Enter mobile number",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(20.dp))


            SubmitButton(
                text = "Create Student Account",
                onClick = {
                    navController.navigate("student_home") {
                        popUpTo("authentication") { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
//
//            SocialLoginDivider()
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            SocialLoginButtons(
//                onGoogleClick = { /* Google login */ },
//                onSecondaryClick = { /* Facebook login */ },
//                secondaryText = "Facebook",
//                secondaryEmoji = "📘"
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
