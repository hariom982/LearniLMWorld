package com.example.learnilmworld.student

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.learnilmworld.models.User
import com.example.learnilmworld.retrofit.mongo_backend.AuthManager
import com.example.learnilmworld.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var profile by remember { mutableStateOf<User?>(null) }

    // ── Saved (displayed) state ───────────────────────────────────────────────
    var savedProfileImageUrl  by remember { mutableStateOf("") }
    var savedFullName         by remember { mutableStateOf("") }
    var savedEmail            by remember { mutableStateOf("") }
    var savedBio              by remember { mutableStateOf("") }
    var savedPhoneNumber      by remember { mutableStateOf("") }
    var savedLocation         by remember { mutableStateOf("") }
    var savedQualification    by remember { mutableStateOf("") }
    var savedCollege          by remember { mutableStateOf("") }
    var savedLanguagesToLearn by remember { mutableStateOf<List<String>>(emptyList()) }
    var savedNativeLanguage   by remember { mutableStateOf("") }
    var savedLearningLevel    by remember { mutableStateOf("") }

    // ── Edit mode + draft state ───────────────────────────────────────────────
    var isEditMode by remember { mutableStateOf(false) }

    var draftProfileImageUrl  by remember { mutableStateOf("") }
    var draftFullName         by remember { mutableStateOf("") }
    var draftEmail            by remember { mutableStateOf("") }
    var draftBio              by remember { mutableStateOf("") }
    var draftPhoneNumber      by remember { mutableStateOf("") }
    var draftLocation         by remember { mutableStateOf("") }
    var draftQualification    by remember { mutableStateOf("") }
    var draftCollege          by remember { mutableStateOf("") }

    fun enterEditMode() {
        draftProfileImageUrl = savedProfileImageUrl
        draftFullName        = savedFullName
        draftEmail           = savedEmail
        draftBio             = savedBio
        draftPhoneNumber     = savedPhoneNumber
        draftLocation        = savedLocation
        draftQualification   = savedQualification
        draftCollege         = savedCollege
        isEditMode = true
    }

    fun confirmEdit() {
        savedProfileImageUrl = draftProfileImageUrl
        savedFullName        = draftFullName
        savedEmail           = draftEmail
        savedBio             = draftBio
        savedPhoneNumber     = draftPhoneNumber
        savedLocation        = draftLocation
        savedQualification   = draftQualification
        savedCollege         = draftCollege
        isEditMode = false
    }

    // ── Load profile ──────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        val firebaseUser = viewModel.getCurrentFirebaseUser()
        if (firebaseUser == null) {
            navController.navigate("choicescreen") { popUpTo(0) { inclusive = true } }
            return@LaunchedEffect
        }

        val tokenResult = firebaseUser.getIdToken(false).await()
        val token = tokenResult.token
        if (token == null) { isLoading = false; return@LaunchedEffect }

        coroutineScope.launch {
            AuthManager.getProfile(token)
                .onSuccess { mongoProfile ->
                    profile = mongoProfile
                    savedFullName         = mongoProfile.fullName + " " + (mongoProfile.lastName ?: "")
                    savedEmail            = mongoProfile.email
                    savedPhoneNumber      = mongoProfile.phoneNumber ?: ""
                    savedLocation         = mongoProfile.location ?: ""
                    savedNativeLanguage   = mongoProfile.nativeLanguage ?: ""
                    savedLearningLevel    = mongoProfile.learningLevel ?: ""
                    savedQualification    = mongoProfile.qualification ?: ""
                    savedCollege          = mongoProfile.college ?: ""
                    savedLanguagesToLearn = mongoProfile.languagesToLearn ?: emptyList()
                    isLoading = false
                }
                .onFailure { isLoading = false }
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3F51B5), Color(0xFF6073E3), Color(0xFFFFF5E1))
                )
            )
    ) {
        if (isLoading) {
            ProfileLoadingShimmer()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // ── Header ────────────────────────────────────────────────────
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Profile",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (!isEditMode) {
                            IconButton(
                                onClick = { enterEditMode() },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                                    .size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = { isEditMode = false }, // discard
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
                                        .size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancel Edit",
                                        tint = Color(0xFFFF8C42),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { confirmEdit() },
                                    modifier = Modifier
                                        .background(Color(0xFF10B981), RoundedCornerShape(12.dp))
                                        .size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Save Profile",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Profile Image ─────────────────────────────────────────────
                item {
                    if (isEditMode) {
                        ProfileImageSection(
                            imageUrl = draftProfileImageUrl,
                            onImageUrlChange = { draftProfileImageUrl = it }
                        )
                    } else {
                        ProfileImageSectionStatic(imageUrl = savedProfileImageUrl)
                    }
                }

                // ── Full Name & Email ─────────────────────────────────────────
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (isEditMode) {
                            ProfileTextField("Full Name", draftFullName, { draftFullName = it }, "Enter your full name", Modifier.weight(1f))
                            ProfileTextField("Email Address", draftEmail, { draftEmail = it }, "Enter your email", Modifier.weight(1f), KeyboardType.Email)
                        } else {
                            ProfileReadField("Full Name", savedFullName, Modifier.weight(1f))
                            ProfileReadField("Email Address", savedEmail, Modifier.weight(1f))
                        }
                    }
                }

                // ── Bio ───────────────────────────────────────────────────────
                item {
                    if (isEditMode) {
                        ProfileTextField("Bio", draftBio, { draftBio = it }, "Tell us about yourself...", singleLine = false, minLines = 4)
                    } else {
                        ProfileReadField("Bio", savedBio.ifBlank { "—" }, multiLine = true)
                    }
                }

                // ── Phone & Location ──────────────────────────────────────────
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (isEditMode) {
                            ProfileTextField("Phone Number", draftPhoneNumber, { draftPhoneNumber = it }, "Enter phone number", Modifier.weight(1f), KeyboardType.Phone)
                            ProfileTextField("Location", draftLocation, { draftLocation = it }, "City, Country", Modifier.weight(1f))
                        } else {
                            ProfileReadField("Phone Number", savedPhoneNumber.ifBlank { "—" }, Modifier.weight(1f))
                            ProfileReadField("Location", savedLocation.ifBlank { "—" }, Modifier.weight(1f))
                        }
                    }
                }

                // ── Qualification & College ───────────────────────────────────
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (isEditMode) {
                            ProfileTextField("Highest Qualification", draftQualification, { draftQualification = it }, "e.g. B.Sc. Computer Science", Modifier.weight(1f))
                            ProfileTextField("College / University", draftCollege, { draftCollege = it }, "College or University name", Modifier.weight(1f))
                        } else {
                            ProfileReadField("Highest Qualification", savedQualification.ifBlank { "—" }, Modifier.weight(1f))
                            ProfileReadField("College / University", savedCollege.ifBlank { "—" }, Modifier.weight(1f))
                        }
                    }
                }

                // ── Save Changes button (edit mode only) ──────────────────────
                if (isEditMode) {
                    item {
                        Button(
                            onClick = { confirmEdit() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D2D44)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 10.dp)
                        ) {
                            Text("Save Changes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // ── Logout (always visible) ───────────────────────────────────
                item {
                    OutlinedButton(
                        onClick = {
                            viewModel.logout()
                            navController.navigate("choicescreen") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = Color(0xFFDC2626)),
                        border = BorderStroke(2.dp, Color(0xFFDC2626)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

// ── Read-only field ───────────────────────────────────────────────────────────
@Composable
fun ProfileReadField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    multiLine: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.75f),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Surface(
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value.ifBlank { "—" },
                fontSize = 15.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = if (multiLine) 14.dp else 16.dp),
                lineHeight = if (multiLine) 22.sp else 15.sp
            )
        }
    }
}

// ── Static image section ──────────────────────────────────────────────────────
@Composable
fun ProfileImageSectionStatic(imageUrl: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Profile Image",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isBlank()) {
                Text("👤", fontSize = 60.sp)
            } else {
                // In a real app, load with Coil/Glide
                Text("👤", fontSize = 60.sp)
            }
        }
    }
}

// ── Shimmer (unchanged) ───────────────────────────────────────────────────────
@Composable
fun ProfileLoadingShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.3f)
    )

    fun getShimmerBrush(width: Float): Brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - width, translateAnim - width),
        end = Offset(translateAnim, translateAnim)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Box(modifier = Modifier.width(200.dp).height(40.dp).clip(RoundedCornerShape(8.dp)).background(getShimmerBrush(200f)))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)).background(getShimmerBrush(140f)))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)).background(getShimmerBrush(300f)))
                    Box(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp)).background(getShimmerBrush(300f)))
                }
            }
        }
        items(3) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(2) {
                    Column(Modifier.weight(1f)) {
                        Box(modifier = Modifier.width(100.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).background(getShimmerBrush(100f)))
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)).background(getShimmerBrush(300f)))
                    }
                }
            }
        }
        item {
            Column {
                Box(modifier = Modifier.width(80.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).background(getShimmerBrush(80f)))
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(getShimmerBrush(400f)))
            }
        }
        items(2) {
            Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp)).background(getShimmerBrush(400f)))
        }
    }
}

// ── Editable image section (unchanged) ───────────────────────────────────────
@Composable
fun ProfileImageSection(
    imageUrl: String,
    onImageUrlChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Profile Image", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (imageUrl.isEmpty()) "No image" else "👤", fontSize = if (imageUrl.isEmpty()) 14.sp else 60.sp, color = Color(0xFF9CA3AF))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = imageUrl,
                    onValueChange = onImageUrlChange,
                    placeholder = { Text("Paste image URL (or upload below)", fontSize = 14.sp, color = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF2D2D44)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE5E7EB))
                ) {
                    Text("Upload Image", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Editable text field (unchanged) ──────────────────────────────────────────
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
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 15.sp, color = Color(0xFF9CA3AF)) },
            modifier = Modifier.fillMaxWidth().then(if (!singleLine) Modifier.height(120.dp) else Modifier.height(56.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color(0xFF2D2D44), unfocusedTextColor = Color(0xFF2D2D44)
            ),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {}
}