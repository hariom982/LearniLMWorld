package com.example.learnilmworld.trainer

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.learnilmworld.models.User
import com.example.learnilmworld.retrofit.mongo_backend.AuthManager
import com.example.learnilmworld.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun TrainerProfileScreen(
    viewModel: AuthViewModel,
    navController: NavHostController
) {
    // ── Persistent (saved) state ──────────────────────────────────────────────
    var profile by remember { mutableStateOf<User?>(null) }
    var savedProfileImageUrl by remember { mutableStateOf("") }
    var savedFullName       by remember { mutableStateOf("") }
    var savedEmail          by remember { mutableStateOf("") }
    var savedPhoneNumber    by remember { mutableStateOf("") }
    var savedNationality    by remember { mutableStateOf("") }
    var savedLocation       by remember { mutableStateOf("") }
    var savedBio            by remember { mutableStateOf("nothing to describe") }
    var savedYearsOfExperience by remember { mutableStateOf("") }
    var savedHourlyRate     by remember { mutableStateOf("25") }
    var savedTeachingStyle  by remember { mutableStateOf("Conversational") }
    var savedIsAvailableForBookings by remember { mutableStateOf(true) }
    var savedLanguages      by remember { mutableStateOf<List<String>>(emptyList()) }
    var savedSpecializations by remember { mutableStateOf(listOf("maths", "english")) }
    var savedStandards      by remember { mutableStateOf(listOf("")) }
    var savedAvailableDays  by remember { mutableStateOf(setOf<String>()) }
    var savedDemoVideoUrl   by remember { mutableStateOf("") }
    var savedInstagramUrl   by remember { mutableStateOf("") }
    var savedYoutubeUrl     by remember { mutableStateOf("") }
    var savedLinkedinUrl    by remember { mutableStateOf("") }

    // ── Edit-mode draft state (copied from saved when edit starts) ────────────
    var isEditMode by remember { mutableStateOf(false) }

    var draftProfileImageUrl by remember { mutableStateOf("") }
    var draftFullName        by remember { mutableStateOf("") }
    var draftEmail           by remember { mutableStateOf("") }
    var draftPhoneNumber     by remember { mutableStateOf("") }
    var draftNationality     by remember { mutableStateOf("") }
    var draftLocation        by remember { mutableStateOf("") }
    var draftBio             by remember { mutableStateOf("") }
    var draftYearsOfExperience by remember { mutableStateOf("") }
    var draftHourlyRate      by remember { mutableStateOf("") }
    var draftTeachingStyle   by remember { mutableStateOf("") }
    var draftIsAvailableForBookings by remember { mutableStateOf(true) }
    var draftLanguages       by remember { mutableStateOf<List<String>>(emptyList()) }
    var draftSpecializations by remember { mutableStateOf<List<String>>(emptyList()) }
    var draftStandards       by remember { mutableStateOf<List<String>>(emptyList()) }
    var draftAvailableDays   by remember { mutableStateOf(setOf<String>()) }
    var draftDemoVideoUrl    by remember { mutableStateOf("") }
    var draftInstagramUrl    by remember { mutableStateOf("") }
    var draftYoutubeUrl      by remember { mutableStateOf("") }
    var draftLinkedinUrl     by remember { mutableStateOf("") }

    var newLanguage       by remember { mutableStateOf("") }
    var newSpecialization by remember { mutableStateOf("") }
    var newStandard       by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val coroutineScope = rememberCoroutineScope()

    // ── Helper: start editing ─────────────────────────────────────────────────
    fun enterEditMode() {
        draftProfileImageUrl        = savedProfileImageUrl
        draftFullName               = savedFullName
        draftEmail                  = savedEmail
        draftPhoneNumber            = savedPhoneNumber
        draftNationality            = savedNationality
        draftLocation               = savedLocation
        draftBio                    = savedBio
        draftYearsOfExperience      = savedYearsOfExperience
        draftHourlyRate             = savedHourlyRate
        draftTeachingStyle          = savedTeachingStyle
        draftIsAvailableForBookings = savedIsAvailableForBookings
        draftLanguages              = savedLanguages
        draftSpecializations        = savedSpecializations
        draftStandards              = savedStandards
        draftAvailableDays          = savedAvailableDays
        draftDemoVideoUrl           = savedDemoVideoUrl
        draftInstagramUrl           = savedInstagramUrl
        draftYoutubeUrl             = savedYoutubeUrl
        draftLinkedinUrl            = savedLinkedinUrl
        newLanguage       = ""
        newSpecialization = ""
        newStandard       = ""
        isEditMode = true
    }

    // ── Helper: confirm edits ─────────────────────────────────────────────────
    fun confirmEdit() {
        savedProfileImageUrl        = draftProfileImageUrl
        savedFullName               = draftFullName
        savedEmail                  = draftEmail
        savedPhoneNumber            = draftPhoneNumber
        savedNationality            = draftNationality
        savedLocation               = draftLocation
        savedBio                    = draftBio
        savedYearsOfExperience      = draftYearsOfExperience
        savedHourlyRate             = draftHourlyRate
        savedTeachingStyle          = draftTeachingStyle
        savedIsAvailableForBookings = draftIsAvailableForBookings
        savedLanguages              = draftLanguages
        savedSpecializations        = draftSpecializations
        savedStandards              = draftStandards
        savedAvailableDays          = draftAvailableDays
        savedDemoVideoUrl           = draftDemoVideoUrl
        savedInstagramUrl           = draftInstagramUrl
        savedYoutubeUrl             = draftYoutubeUrl
        savedLinkedinUrl            = draftLinkedinUrl
        isEditMode = false
    }

    // ── Load profile ──────────────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        val currentFirebaseUser = viewModel.getCurrentFirebaseUser()
        if (currentFirebaseUser == null) {
            navController.navigate("login")
            return@LaunchedEffect
        }

        val tokenResult = currentFirebaseUser.getIdToken(false).await()
        val token = tokenResult.token ?: run {
            Toast.makeText(navController.context, "Failed to get auth token", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }

        AuthManager.getProfile(token)
            .onSuccess { mongoProfile ->
                profile = mongoProfile
                savedFullName               = mongoProfile.fullName + " " + mongoProfile.lastName
                savedEmail                  = mongoProfile.email
                savedPhoneNumber            = mongoProfile.phoneNumber ?: ""
                savedNationality            = mongoProfile.nationality ?: ""
                savedLocation               = mongoProfile.location ?: ""
                savedBio                    = mongoProfile.bio ?: "nothing to describe"
                savedYearsOfExperience      = mongoProfile.yearsOfExperience?.toString() ?: ""
                savedHourlyRate             = mongoProfile.hourlyRate?.toString() ?: "25"
                savedTeachingStyle          = mongoProfile.teachingStyle ?: "Conversational"
                savedIsAvailableForBookings = mongoProfile.isAvailableForBookings ?: true
                savedLanguages              = mongoProfile.languagesToTeach ?: mongoProfile.languagesToLearn ?: emptyList()
                savedSpecializations        = mongoProfile.specializations ?: emptyList()
                isLoading = false
            }
            .onFailure { exception ->
                isLoading = false
                Toast.makeText(navController.context, "Failed to load profile: ${exception.message}", Toast.LENGTH_LONG).show()
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
            TrainerProfileLoadingShimmer()
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
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (!isEditMode) {
                            // Edit icon button
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
                            // Save & Cancel icons
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
                        TrainerProfileImageSection(
                            imageUrl = draftProfileImageUrl,
                            onImageUrlChange = { draftProfileImageUrl = it }
                        )
                    } else {
                        TrainerProfileImageSectionStatic(imageUrl = savedProfileImageUrl)
                    }
                }

                // ── Basic Information ─────────────────────────────────────────
                item {
                    Text("Basic Information", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (isEditMode) {
                            TrainerProfileTextField("Full Name", draftFullName, { draftFullName = it }, "Enter your full name", Modifier.weight(1f))
                            TrainerProfileTextField("Email Address", draftEmail, { draftEmail = it }, "Enter your email", Modifier.weight(1f))
                        } else {
                            TrainerProfileReadField("Full Name", savedFullName, Modifier.weight(1f))
                            TrainerProfileReadField("Email Address", savedEmail, Modifier.weight(1f))
                        }
                    }
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (isEditMode) {
                            TrainerProfileTextField("Phone Number", draftPhoneNumber, { draftPhoneNumber = it }, "Enter phone number", Modifier.weight(1f))
                            TrainerProfileDropdown("Nationality", draftNationality, { draftNationality = it },
                                listOf("Indian", "American", "British", "Canadian", "Australian"), "Select Nationality", Modifier.weight(1f))
                        } else {
                            TrainerProfileReadField("Phone Number", savedPhoneNumber, Modifier.weight(1f))
                            TrainerProfileReadField("Nationality", savedNationality, Modifier.weight(1f))
                        }
                    }
                }

                item {
                    if (isEditMode) TrainerProfileTextField("Location", draftLocation, { draftLocation = it }, "City, Country")
                    else TrainerProfileReadField("Location", savedLocation)
                }

                item {
                    if (isEditMode) TrainerProfileTextField("Bio", draftBio, { draftBio = it }, "Tell us about yourself...", singleLine = false, minLines = 4)
                    else TrainerProfileReadField("Bio", savedBio, multiLine = true)
                }

                // ── Teaching Information ──────────────────────────────────────
                item {
                    Text("Teaching Information", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 16.dp))
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (isEditMode) {
                            TrainerProfileTextField("Years of Experience", draftYearsOfExperience, { draftYearsOfExperience = it }, "5", Modifier.weight(1f))
                            TrainerProfileTextField("Hourly Rate ($)", draftHourlyRate, { draftHourlyRate = it }, "25", Modifier.weight(1f))
                        } else {
                            TrainerProfileReadField("Years of Experience", savedYearsOfExperience, Modifier.weight(1f))
                            TrainerProfileReadField("Hourly Rate ($)", savedHourlyRate, Modifier.weight(1f))
                        }
                    }
                }

                item {
                    if (isEditMode) TrainerProfileDropdown("Teaching Style", draftTeachingStyle, { draftTeachingStyle = it },
                        listOf("Conversational", "Structured", "Interactive", "Exam-focused"), "Select Teaching Style")
                    else TrainerProfileReadField("Teaching Style", savedTeachingStyle)
                }

                // Available for bookings
                item {
                    Column {
                        Text("Is Available for New Bookings", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                        if (isEditMode) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Checkbox(
                                    checked = draftIsAvailableForBookings,
                                    onCheckedChange = { draftIsAvailableForBookings = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                                )
                                Text("Yes", fontSize = 15.sp, color = Color(0xFF2D2D44))
                            }
                        } else {
                            Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                if (savedIsAvailableForBookings) Color(0xFF10B981) else Color(0xFFDC2626),
                                                RoundedCornerShape(50)
                                            )
                                    )
                                    Text(
                                        text = if (savedIsAvailableForBookings) "Available for new bookings" else "Not available for bookings",
                                        fontSize = 15.sp,
                                        color = Color(0xFF2D2D44)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Languages ─────────────────────────────────────────────────
                item {
                    if (isEditMode) {
                        DynamicListSection(
                            title = "Languages",
                            items = draftLanguages,
                            newItemValue = newLanguage,
                            onNewItemChange = { newLanguage = it },
                            onAddItem = { if (newLanguage.isNotBlank()) { draftLanguages = draftLanguages + newLanguage.trim(); newLanguage = "" } },
                            onRemoveItem = { draftLanguages = draftLanguages - it },
                            placeholder = "Add new language"
                        )
                    } else {
                        StaticTagSection("Languages", savedLanguages)
                    }
                }

                // ── Specializations ───────────────────────────────────────────
                item {
                    if (isEditMode) {
                        DynamicListSection(
                            title = "Specializations",
                            items = draftSpecializations,
                            newItemValue = newSpecialization,
                            onNewItemChange = { newSpecialization = it },
                            onAddItem = { if (newSpecialization.isNotBlank()) { draftSpecializations = draftSpecializations + newSpecialization.trim(); newSpecialization = "" } },
                            onRemoveItem = { draftSpecializations = draftSpecializations - it },
                            placeholder = "Add new specialization"
                        )
                    } else {
                        StaticTagSection("Specializations", savedSpecializations)
                    }
                }

                // ── Standards ─────────────────────────────────────────────────
                item {
                    if (isEditMode) {
                        DynamicListSection(
                            title = "Standards (e.g., 5-8, 5-10, etc.)",
                            items = draftStandards,
                            newItemValue = newStandard,
                            onNewItemChange = { newStandard = it },
                            onAddItem = { if (newStandard.isNotBlank()) { draftStandards = draftStandards + newStandard.trim(); newStandard = "" } },
                            onRemoveItem = { draftStandards = draftStandards - it },
                            placeholder = "Add new standard (e.g., 5-8)"
                        )
                    } else {
                        StaticTagSection("Standards", savedStandards)
                    }
                }

                // ── Availability ──────────────────────────────────────────────
                item {
                    Text("Availability", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                }

                items(daysOfWeek.size) { index ->
                    val day = daysOfWeek[index]
                    if (isEditMode) {
                        AvailabilityDayItem(
                            day = day,
                            isAvailable = draftAvailableDays.contains(day),
                            onToggle = {
                                draftAvailableDays = if (draftAvailableDays.contains(day)) draftAvailableDays - day else draftAvailableDays + day
                            }
                        )
                    } else {
                        AvailabilityDayItemStatic(day = day, isAvailable = savedAvailableDays.contains(day))
                    }
                }

                // ── Media & Social Links ──────────────────────────────────────
                item {
                    Text("Media & Social Links", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 16.dp))
                }

                item {
                    if (isEditMode) TrainerProfileTextField("Demo Video URL (YouTube)", draftDemoVideoUrl, { draftDemoVideoUrl = it }, "https://www.youtube.com/watch?v=...")
                    else TrainerProfileReadField("Demo Video URL (YouTube)", savedDemoVideoUrl.ifBlank { "—" })
                }

                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (isEditMode) {
                            TrainerProfileTextField("Instagram URL", draftInstagramUrl, { draftInstagramUrl = it }, "https://instagram.com/username", Modifier.weight(1f))
                            TrainerProfileTextField("YouTube URL", draftYoutubeUrl, { draftYoutubeUrl = it }, "https://youtube.com/channel/...", Modifier.weight(1f))
                        } else {
                            TrainerProfileReadField("Instagram URL", savedInstagramUrl.ifBlank { "—" }, Modifier.weight(1f))
                            TrainerProfileReadField("YouTube URL", savedYoutubeUrl.ifBlank { "—" }, Modifier.weight(1f))
                        }
                    }
                }

                item {
                    if (isEditMode) TrainerProfileTextField("LinkedIn URL", draftLinkedinUrl, { draftLinkedinUrl = it }, "https://linkedin.com/in/username")
                    else TrainerProfileReadField("LinkedIn URL", savedLinkedinUrl.ifBlank { "—" })
                }

                // ── Edit-mode action buttons (Update / Reset) ─────────────────
                if (isEditMode) {
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { confirmEdit() },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C42)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Update Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = { enterEditMode() }, // reset drafts back to saved
                                modifier = Modifier.weight(0.5f).height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6B7280)),
                                border = BorderStroke(2.dp, Color(0xFF6B7280)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Reset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
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
fun TrainerProfileReadField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    multiLine: Boolean = false
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.75f), modifier = Modifier.padding(bottom = 6.dp))
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

// ── Static tag list (read-only) ───────────────────────────────────────────────
@Composable
fun StaticTagSection(title: String, items: List<String>) {
    Column {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 10.dp))
        if (items.isEmpty()) {
            Text("None added", fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f))
        } else {
            // Wrap tags in a FlowRow-like horizontal layout using wrapping rows
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { item ->
                    Surface(color = Color.White.copy(alpha = 0.18f), shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = item,
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

// ── Static image section (read-only) ─────────────────────────────────────────
@Composable
fun TrainerProfileImageSectionStatic(imageUrl: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Profile image", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (imageUrl.isBlank()) "No image" else "Image set", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

// ── Static availability row (read-only) ──────────────────────────────────────
@Composable
fun AvailabilityDayItemStatic(day: String, isAvailable: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isAvailable) Color(0xFF10B981).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(day, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Text(
                text = if (isAvailable) "✓  Available" else "Unavailable",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isAvailable) Color(0xFF10B981) else Color.White.copy(alpha = 0.45f)
            )
        }
    }
}

// ── All original composables below (unchanged) ────────────────────────────────

@Composable
fun TrainerProfileLoadingShimmer() {
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

    fun getShimmerBrush(width: Float): Brush {
        return Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - width, translateAnim - width),
            end = Offset(translateAnim, translateAnim)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Box(modifier = Modifier.width(200.dp).height(40.dp).clip(RoundedCornerShape(8.dp)).background(getShimmerBrush(200f)))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp)).background(getShimmerBrush(140f)))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)).background(getShimmerBrush(300f)))
                    Box(modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(8.dp)).background(getShimmerBrush(300f)))
                }
            }
        }
        items(4) {
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
            Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(16.dp)).background(getShimmerBrush(400f)))
        }
    }
}

@Composable
fun TrainerProfileImageSection(
    imageUrl: String,
    onImageUrlChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Profile image", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(140.dp).background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No image", fontSize = 14.sp, color = Color(0xFF9CA3AF))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = imageUrl,
                    onValueChange = onImageUrlChange,
                    placeholder = { Text("Paste image URL (or upload below)", fontSize = 14.sp, color = Color(0xFF9CA3AF)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2D2D44), containerColor = Color.White)
                ) {
                    Text("Upload image", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun TrainerProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 15.sp, color = Color(0xFF9CA3AF)) },
            modifier = Modifier.fillMaxWidth().then(if (!singleLine) Modifier.heightIn(min = 120.dp) else Modifier),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color(0xFF2D2D44), unfocusedTextColor = Color(0xFF2D2D44)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            minLines = if (!singleLine) minLines else 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerProfileDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = if (value.isEmpty()) placeholder else value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = { onValueChange(option); expanded = false })
                }
            }
        }
    }
}

@Composable
fun DynamicListSection(
    title: String,
    items: List<String>,
    newItemValue: String,
    onNewItemChange: (String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (String) -> Unit,
    placeholder: String
) {
    Column {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp)).padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item, fontSize = 15.sp, color = Color(0xFF2D2D44))
                TextButton(onClick = { onRemoveItem(item) }) {
                    Text("Remove", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newItemValue,
                onValueChange = onNewItemChange,
                placeholder = { Text(placeholder, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Button(onClick = onAddItem, modifier = Modifier.height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C42)), shape = RoundedCornerShape(12.dp)) {
                Text("Add", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun AvailabilityDayItem(day: String, isAvailable: Boolean, onToggle: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFF9FAFB), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(day, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2D2D44))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Checkbox(checked = isAvailable, onCheckedChange = { onToggle() }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981)))
                Text("Available", fontSize = 15.sp, color = Color(0xFF2D2D44))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainerProfileScreenPreview() {
    MaterialTheme {}
}