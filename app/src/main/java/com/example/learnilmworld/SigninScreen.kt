package com.example.learnilmworld

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.learnilmworld.trainer.ExperienceSelector
import com.example.learnilmworld.trainer.FormTextField
import com.example.learnilmworld.trainer.LanguageChipSelector
import com.example.learnilmworld.trainer.SignupHeader
import com.example.learnilmworld.trainer.SubmitButton

@Composable
fun SigninScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


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
                title = "Welcome Back",
                subtitle = "Sign in to continue your journey"
            )

            Spacer(modifier = Modifier.height(32.dp))

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
            Text(
                text = "Forgot password?",
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 10.dp, end = 2.dp),
                color = Color.White,
                textDecoration = TextDecoration.Underline
            )
            Spacer(modifier = Modifier.height(20.dp))

            SubmitButton(
                text = "Sign In",
                onClick = {
//                    // Handle student signup
//                    println("Student signup: $firstName $lastName")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
