//package com.example.learnilmworld.retrofit.mongo_backend
//
//import android.util.Log
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.input.*
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
//import com.google.firebase.auth.FirebaseAuthInvalidUserException
//import com.google.firebase.auth.FirebaseAuthUserCollisionException
//import com.google.firebase.auth.FirebaseAuthWeakPasswordException
//import kotlinx.coroutines.launch
//
//
//
//@Composable
//fun TempLoginScreen(navController: NavController) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var isPasswordVisible by remember { mutableStateOf(false) }
//    var isLoading by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    var successMessage by remember { mutableStateOf<String?>(null) }
//    var token by remember { mutableStateOf<String?>(null) }
//    var name by remember { mutableStateOf("") }
//    var result by remember { mutableStateOf("") }
//    val coroutineScope = rememberCoroutineScope()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp)
//            .verticalScroll(rememberScrollState()),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Welcome",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 32.dp)
//        )
//
//        // Email Field
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth(),
//            isError = errorMessage != null && email.isBlank()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = { Text("Name") },
//            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth(),
//            isError = errorMessage != null && name.isBlank()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Password Field
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
//            trailingIcon = {
//                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
//                    Icon(
//                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
//                    )
//                }
//            },
//            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth(),
//            isError = errorMessage != null && password.length < 6
//        )
//
//        if (errorMessage != null && password.length < 6) {
//            Text(
//                text = "Password must be at least 6 characters",
//                color = MaterialTheme.colorScheme.error,
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.align(Alignment.Start).padding(start = 16.dp, top = 4.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Loading & Messages
//        if (isLoading) {
//            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//        }
//
//        errorMessage?.let {
//            Text(
//                text = it,
//                color = MaterialTheme.colorScheme.error,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//        }
//
//        successMessage?.let {
//            Text(
//                text = it,
//                color = Color.Green,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//        }
//
//        // Login Button
//        Button(
//            onClick = {
//                if (email.isBlank() || password.length < 6 || name.isBlank()) {
//                    errorMessage = "Please fill all fields (password 6+ chars)"
//                    return@Button
//                }
//
//                isLoading = true
//                errorMessage = null
//                successMessage = null
//
//                coroutineScope.launch {
//                    AuthManager.signUp(email, password)
//                        .onSuccess { token ->  // Now you get the token here!
//                            // Save profile using the fresh token
//                            AuthManager.saveProfile(token, user)
//                                .onSuccess {
//                                    isLoading = false
//                                    successMessage = "Account created and profile saved!"
//                                    // Optionally auto-login and navigate
//                                    navController.navigate("home") {
//                                        popUpTo("login") { inclusive = true }
//                                    }
//                                }
//                                .onFailure { exception ->
//                                    isLoading = false
//                                    errorMessage = "Profile save failed: ${exception.message}"
//                                }
//                        }
//                        .onFailure { exception ->
//                            isLoading = false
//                            errorMessage = when (exception) {
//                                is FirebaseAuthWeakPasswordException -> "Password is too weak"
//                                is FirebaseAuthUserCollisionException -> "Email already in use"
//                                else -> exception.message ?: "Sign up failed"
//                            }
//                            Log.e("ProfileSave", "Failed: ${exception.message}")
//                        }
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isLoading
//        ) {
//            Text("Login")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Sign Up Button
//        OutlinedButton(
//            onClick = {
//                if (email.isBlank() || password.length < 6 || name.isBlank()) {
//                    errorMessage = "Please fill all fields (password 6+ chars)"
//                    return@OutlinedButton
//                }
//
//                isLoading = true
//                errorMessage = null
//                successMessage = null
//
//                coroutineScope.launch {
//                    AuthManager.signUp(email, password)
//                        .onSuccess { token ->  // Now you get the token here!
//                            // Save profile using the fresh token
//                            AuthManager.saveProfile(token, name)
//                                .onSuccess {
//                                    isLoading = false
//                                    successMessage = "Account created and profile saved!"
//                                }
//                                .onFailure { exception ->
//                                    isLoading = false
//                                    errorMessage = "Profile save failed: ${exception.message}"
//                                }
//                        }
//                        .onFailure { exception ->
//                            isLoading = false
//                            errorMessage = when (exception) {
//                                is FirebaseAuthWeakPasswordException -> "Password is too weak"
//                                is FirebaseAuthUserCollisionException -> "Email already in use"
//                                else -> exception.message ?: "Sign up failed"
//                            }
//                            Log.e("ProfileSave", "Failed: ${exception.message}")
//                        }
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = !isLoading
//        ) {
//            Text("Create Account")
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//    }
//}