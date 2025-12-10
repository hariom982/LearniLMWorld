package com.example.learnilmworld.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.learnilmworld.viewModel.AuthState
import com.example.learnilmworld.viewModel.AuthViewModel


@Composable
fun ProfileScreen(viewModel: AuthViewModel,
                  navController: NavHostController) {
    val currentUser by viewModel.currentUser.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var profileImageUrl by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            // Set your state variables with user data
            fullName = user.fullName
            email = user.email
            phoneNumber = user.phoneNumber
            email = user.email
            bio = user.bio
            location = user.location
            qualification = user.qualification
            college = user.college
        }
    }

//    LaunchedEffect(authState) {
//        when (authState) {
//            is AuthState.Error -> {
//                // Navigate to choice screen and clear back stack
//                navController.navigate("choice") {
//                    popUpTo(0) { inclusive = true }
//                }
//                viewModel.resetState()
//            }
//            else -> {}
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD4A574),
                        Color(0xFFE6B87D)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Text(
                    text = "My Profile",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D2D44),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Profile Image Section
            item {
                ProfileImageSection(
                    imageUrl = profileImageUrl,
                    onImageUrlChange = { profileImageUrl = it }
                )
            }

            // Full Name and Email Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileTextField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "Enter your full name",
                        modifier = Modifier.weight(1f)
                    )

                    ProfileTextField(
                        label = "Email Address",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Enter your email",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Email
                    )
                }
            }

            // Bio
            item {
                ProfileTextField(
                    label = "Bio",
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = "Tell us about yourself...",
                    singleLine = false,
                    minLines = 4
                )
            }

            // Phone Number and Location Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileTextField(
                        label = "Phone Number",
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = "Enter phone number",
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Phone
                    )

                    ProfileTextField(
                        label = "Location",
                        value = location,
                        onValueChange = { location = it },
                        placeholder = "City, Country",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Qualification and College Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProfileTextField(
                        label = "Highest Qualification",
                        value = qualification,
                        onValueChange = { qualification = it },
                        placeholder = "e.g. B.Sc. Computer Science",
                        modifier = Modifier.weight(1f)
                    )

                    ProfileTextField(
                        label = "College / University",
                        value = college,
                        onValueChange = { college = it },
                        placeholder = "College or University name",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Save Button
            item {
                Button(
                    onClick = {
                        // Handle save profile
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D2D44)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp
                    )
                ) {
                    Text(
                        text = "Save Changes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Logout Button
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.logout()
                        navController.navigate("choicescreen") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFFDC2626)
                    ),
                    border = BorderStroke(2.dp, Color(0xFFDC2626)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logout",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ProfileImageSection(
    imageUrl: String,
    onImageUrlChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Profile Image",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D44),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Preview
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl.isEmpty()) {
                    Text(
                        text = "No image",
                        fontSize = 14.sp,
                        color = Color(0xFF9CA3AF)
                    )
                } else {
                    // In real app, load image from URL
                    Text(
                        text = "👤",
                        fontSize = 60.sp
                    )
                }
            }

            // URL Input and Upload Button
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = imageUrl,
                    onValueChange = onImageUrlChange,
                    placeholder = {
                        Text(
                            "Paste image URL (or upload below)",
                            fontSize = 14.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Button(
                    onClick = { /* Handle image upload */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2D2D44)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE5E7EB))
                ) {
                    Text(
                        text = "Upload Image",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D44),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    fontSize = 15.sp,
                    color = Color(0xFF9CA3AF)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!singleLine) Modifier.height(120.dp)
                    else Modifier.height(56.dp)
                ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color(0xFF2D2D44),
                unfocusedTextColor = Color(0xFF2D2D44)
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

