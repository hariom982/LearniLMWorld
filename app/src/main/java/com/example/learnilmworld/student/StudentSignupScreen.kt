package com.example.learnilmworld.student

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.learnilmworld.trainer.FormTextField
import com.example.learnilmworld.trainer.SignupHeader
import com.example.learnilmworld.viewModel.AuthState
import com.example.learnilmworld.viewModel.AuthViewModel

@Composable
fun StudentSignupScreen(navController: NavHostController,
                        viewModel: AuthViewModel) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneno by remember { mutableStateOf("") }
    var nativeLanguage by remember { mutableStateOf("") }
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }
    var learningLevel by remember { mutableStateOf("") }
    val location by remember { mutableStateOf("No address") }
    val qualification by  remember { mutableStateOf("No qualification") }
    val college by remember { mutableStateOf("No College") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var phonenoError by remember { mutableStateOf(false) }

    val isLoading = authState is AuthState.Loading

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate("student_home") {
                    popUpTo("student_signup") { inclusive = true }
                }
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    fun validateFields(): Boolean {
        firstNameError = firstName.isBlank()
        lastNameError = lastName.isBlank()
        emailError = email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        passwordError = password.isBlank() || password.length < 6
        confirmPasswordError = confirmPassword.isBlank() || password != confirmPassword
        phonenoError = phoneno.isBlank()

        return !firstNameError && !lastNameError && !emailError &&
                !passwordError && !confirmPasswordError && !phonenoError
    }

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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FormTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = false
                    },
                    label = "First Name",
                    placeholder = "First name",
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        lastNameError = false
                    },
                    label = "Last Name",
                    placeholder = "Last name",
                    modifier = Modifier.weight(1f)
                )
            }
            if (firstNameError || lastNameError) {
                Text(
                    text = "First and last name are required",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = "Email Address",
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email
            )
            if (emailError) {
                Text(
                    text = "Please enter a valid email address",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password field with eye icon
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = { Text("Password") },
                placeholder = { Text("Enter password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF667eea)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.95f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color(0xFF667eea),
                    unfocusedTextColor = Color(0xFF667eea),
                    focusedLabelColor = Color(0xFF667eea),
                    unfocusedLabelColor = Color(0xFF667eea).copy(alpha = 0.7f),
                    focusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f),
                    errorPlaceholderColor = Color.Red,
                    errorBorderColor = Color.Red,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            if (passwordError) {
                Text(
                    text = "Password must be at least 6 characters",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm Password field with eye icon
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = false
                },
                label = { Text("Confirm Password") },
                placeholder = { Text("Confirm password") },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF667eea)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.95f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color(0xFF667eea),
                    unfocusedTextColor = Color(0xFF667eea),
                    focusedLabelColor = Color(0xFF667eea),
                    unfocusedLabelColor = Color(0xFF667eea).copy(alpha = 0.7f),
                    focusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f),
                    unfocusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            if (confirmPasswordError) {
                Text(
                    text = if (confirmPassword.isBlank()) "Please confirm your password" else "Passwords do not match",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = phoneno,
                onValueChange = {
                    phoneno = it
                    phonenoError = false
                },
                label = "Mobile Number",
                placeholder = "Enter mobile number",
                keyboardType = KeyboardType.Phone
            )
            if (phonenoError) {
                Text(
                    text = "Mobile number is required",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (validateFields()) {
                        viewModel.registerStudent(
                            email = email,
                            password = password,
                            fullName = firstName,
                            lastName = lastName,
                            phoneNumber = phoneno,
                            nativeLanguage = nativeLanguage,
                            languagesToLearn = selectedLanguages.toList(),
                            learningLevel = learningLevel,
                            location = location,
                            qualification = qualification,
                            college = college
                        )
                    } else {
                        Toast.makeText(context, "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(10.dp, RoundedCornerShape(18.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF667eea)
                    )
                } else {
                    Text(
                        text = "Create Student Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667eea),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}