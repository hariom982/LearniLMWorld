package com.example.learnilmworld.trainer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TrainerProfileScreen() {
    var profileImageUrl by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("Hariom Rajak") }
    var email by remember { mutableStateOf("functionaldevelopers314@gmail.com") }
    var phoneNumber by remember { mutableStateOf("918305005503") }
    var nationality by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("nothing to describe") }
    var yearsOfExperience by remember { mutableStateOf("5") }
    var hourlyRate by remember { mutableStateOf("25") }
    var teachingStyle by remember { mutableStateOf("Conversational") }
    var isAvailableForBookings by remember { mutableStateOf(true) }
    var languages by remember { mutableStateOf(listOf("english", "hindi")) }
    var newLanguage by remember { mutableStateOf("") }
    var specializations by remember { mutableStateOf(listOf("maths", "english")) }
    var newSpecialization by remember { mutableStateOf("") }
    var standards by remember { mutableStateOf(listOf("5-12")) }
    var newStandard by remember { mutableStateOf("") }
    var demoVideoUrl by remember { mutableStateOf("") }
    var instagramUrl by remember { mutableStateOf("") }
    var youtubeUrl by remember { mutableStateOf("") }
    var linkedinUrl by remember { mutableStateOf("") }

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var availableDays by remember { mutableStateOf(setOf<String>()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background( brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD4A574),
                    Color(0xFFE6B87D)
                )
            ))
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
                TrainerProfileImageSection(
                    imageUrl = profileImageUrl,
                    onImageUrlChange = { profileImageUrl = it }
                )
            }

            // Basic Information Section
            item {
                Text(
                    text = "Basic Information",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44)
                )
            }

            // Full Name and Email Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TrainerProfileTextField(
                        label = "Full Name",
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "Enter your full name",
                        modifier = Modifier.weight(1f)
                    )

                    TrainerProfileTextField(
                        label = "Email Address",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "Enter your email",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Phone Number and Nationality Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TrainerProfileTextField(
                        label = "Phone Number",
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        placeholder = "Enter phone number",
                        modifier = Modifier.weight(1f)
                    )

                    TrainerProfileDropdown(
                        label = "Nationality",
                        value = nationality,
                        onValueChange = { nationality = it },
                        options = listOf("Indian", "American", "British", "Canadian", "Australian"),
                        placeholder = "Select Nationality",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Location
            item {
                TrainerProfileTextField(
                    label = "Location",
                    value = location,
                    onValueChange = { location = it },
                    placeholder = "City, Country"
                )
            }

            // Bio
            item {
                TrainerProfileTextField(
                    label = "Bio",
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = "Tell us about yourself...",
                    singleLine = false,
                    minLines = 4
                )
            }

            // Teaching Information Section
            item {
                Text(
                    text = "Teaching Information",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Years of Experience and Hourly Rate
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TrainerProfileTextField(
                        label = "Years of Experience",
                        value = yearsOfExperience,
                        onValueChange = { yearsOfExperience = it },
                        placeholder = "5",
                        modifier = Modifier.weight(1f)
                    )

                    TrainerProfileTextField(
                        label = "Hourly Rate ($)",
                        value = hourlyRate,
                        onValueChange = { hourlyRate = it },
                        placeholder = "25",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Teaching Style
            item {
                TrainerProfileDropdown(
                    label = "Teaching Style",
                    value = teachingStyle,
                    onValueChange = { teachingStyle = it },
                    options = listOf("Conversational", "Structured", "Interactive", "Exam-focused"),
                    placeholder = "Select Teaching Style"
                )
            }

            // Is Available for New Bookings
            item {
                Column {
                    Text(
                        text = "Is Available for New Bookings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D2D44),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isAvailableForBookings,
                            onCheckedChange = { isAvailableForBookings = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF10B981)
                            )
                        )
                        Text(
                            text = "Yes",
                            fontSize = 15.sp,
                            color = Color(0xFF2D2D44)
                        )
                    }
                }
            }

            // Languages Section
            item {
                DynamicListSection(
                    title = "Languages",
                    items = languages,
                    newItemValue = newLanguage,
                    onNewItemChange = { newLanguage = it },
                    onAddItem = {
                        if (newLanguage.isNotBlank()) {
                            languages = languages + newLanguage.trim()
                            newLanguage = ""
                        }
                    },
                    onRemoveItem = { language ->
                        languages = languages - language
                    },
                    placeholder = "Add new language"
                )
            }

            // Specializations Section
            item {
                DynamicListSection(
                    title = "Specializations",
                    items = specializations,
                    newItemValue = newSpecialization,
                    onNewItemChange = { newSpecialization = it },
                    onAddItem = {
                        if (newSpecialization.isNotBlank()) {
                            specializations = specializations + newSpecialization.trim()
                            newSpecialization = ""
                        }
                    },
                    onRemoveItem = { spec ->
                        specializations = specializations - spec
                    },
                    placeholder = "Add new specialization"
                )
            }

            // Standards Section
            item {
                DynamicListSection(
                    title = "Standards (e.g., 5-8, 5-10, etc.)",
                    items = standards,
                    newItemValue = newStandard,
                    onNewItemChange = { newStandard = it },
                    onAddItem = {
                        if (newStandard.isNotBlank()) {
                            standards = standards + newStandard.trim()
                            newStandard = ""
                        }
                    },
                    onRemoveItem = { standard ->
                        standards = standards - standard
                    },
                    placeholder = "Add new standard (e.g., 5-8)"
                )
            }

            // Availability Section
            item {
                Text(
                    text = "Availability",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            // Days of Week
            items(daysOfWeek.size) { index ->
                val day = daysOfWeek[index]
                AvailabilityDayItem(
                    day = day,
                    isAvailable = availableDays.contains(day),
                    onToggle = {
                        availableDays = if (availableDays.contains(day)) {
                            availableDays - day
                        } else {
                            availableDays + day
                        }
                    }
                )
            }

            // Media & Social Links Section
            item {
                Text(
                    text = "Media & Social Links",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Demo Video URL
            item {
                TrainerProfileTextField(
                    label = "Demo Video URL (YouTube)",
                    value = demoVideoUrl,
                    onValueChange = { demoVideoUrl = it },
                    placeholder = "https://www.youtube.com/watch?v=..."
                )
            }

            // Instagram and YouTube URLs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TrainerProfileTextField(
                        label = "Instagram URL",
                        value = instagramUrl,
                        onValueChange = { instagramUrl = it },
                        placeholder = "https://instagram.com/username",
                        modifier = Modifier.weight(1f)
                    )

                    TrainerProfileTextField(
                        label = "YouTube URL",
                        value = youtubeUrl,
                        onValueChange = { youtubeUrl = it },
                        placeholder = "https://youtube.com/channel/...",
                        modifier = Modifier.weight(1f))
                }
            }

            // LinkedIn URL
            item {
                TrainerProfileTextField(
                    label = "LinkedIn URL",
                    value = linkedinUrl,
                    onValueChange = { linkedinUrl = it },
                    placeholder = "https://linkedin.com/in/username"
                )
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* Handle update profile */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF8C42)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Update Profile",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = { /* Handle reset */ },
                        modifier = Modifier
                            .weight(0.5f)
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF6B7280)
                        ),
                        border = BorderStroke(2.dp, Color(0xFF6B7280)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Reset",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// All supporting composables below (unchanged, just properly formatted)
@Composable
fun TrainerProfileImageSection(
    imageUrl: String,
    onImageUrlChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Profile image",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D44),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No image",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = imageUrl,
                    onValueChange = onImageUrlChange,
                    placeholder = {
                        Text("Paste image URL (or upload below)", fontSize = 14.sp, color = Color(0xFF9CA3AF))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedButton(
                    onClick = { /* Handle image upload */ },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2D2D44), containerColor = Color.White),


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
            placeholder = { Text(placeholder, fontSize = 15.sp, color = Color(0xFF9CA3AF)) },
            modifier = Modifier
                .fillMaxWidth()
                .then(if (!singleLine) Modifier.heightIn(min = 120.dp) else Modifier),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color(0xFF2D2D44),
                unfocusedTextColor = Color(0xFF2D2D44)
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
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D44),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = if (value.isEmpty()) placeholder else value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )

            ExposedDropdownMenu(expanded = expanded, onDismissRequest = {expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
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
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D44),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        items.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item, fontSize = 15.sp, color = Color(0xFF2D2D44))
                TextButton(onClick = { onRemoveItem(item) }) {
                    Text("Remove", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newItemValue,
                onValueChange = onNewItemChange,
                placeholder = { Text(placeholder, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Button(
                onClick = onAddItem,
                modifier = Modifier.height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C42)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun AvailabilityDayItem(
    day: String,
    isAvailable: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = day,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D44)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = isAvailable,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                )
                Text("Available", fontSize = 15.sp, color = Color(0xFF2D2D44))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainerProfileScreenPreview() {
    MaterialTheme {
    }
}
