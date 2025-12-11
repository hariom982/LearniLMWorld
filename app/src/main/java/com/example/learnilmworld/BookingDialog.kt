package com.example.learnilmworld

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.learnilmworld.models.SessionRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDialog(
    trainerName: String,
    trainerId: String,
    hourlyRate: Double,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var selectedTimeSlot by remember { mutableStateOf<String?>(null) }
    var selectedDuration by remember { mutableStateOf(60) }
    var description by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("beginner") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val coroutineScope = rememberCoroutineScope()

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val timeSlots = listOf(
        "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
        "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM",
        "04:00 PM", "05:00 PM", "06:00 PM", "07:00 PM",
        "08:00 PM", "09:00 PM"
    )

    val durations = listOf(30, 60, 90, 120)
    val levels = listOf("beginner", "intermediate", "advanced")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Book Session",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D2D44)
                            )
                            Text(
                                text = "with $trainerName",
                                fontSize = 14.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                // Date Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Select Date",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D2D44)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Next 7 days
                            for (i in 0..6) {
                                val calendar = Calendar.getInstance()
                                calendar.add(Calendar.DAY_OF_YEAR, i)
                                val date = calendar.time
                                val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                                val dateNumFormat = SimpleDateFormat("dd", Locale.getDefault())

                                val isSelected = selectedDate?.let {
                                    val selectedCal = Calendar.getInstance().apply { time = it }
                                    val currentCal = Calendar.getInstance().apply { time = date }
                                    selectedCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                                            selectedCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR)
                                } ?: false

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedDate = date },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Color(0xFF3D3D5C) else Color(0xFFF3F4F6)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = dayFormat.format(date),
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else Color(0xFF6B7280)
                                        )
                                        Text(
                                            text = dateNumFormat.format(date),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color(0xFFB8E986) else Color(0xFF2D2D44)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Time Slot Selection
                item {
                    AnimatedVisibility(visible = selectedDate != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Select Time Slot",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D2D44)
                            )

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier.height(200.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(timeSlots) { slot ->
                                    Surface(
                                        modifier = Modifier.clickable { selectedTimeSlot = slot },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (selectedTimeSlot == slot) Color(0xFF3D3D5C) else Color(0xFFF3F4F6)
                                    ) {
                                        Text(
                                            text = slot,
                                            fontSize = 12.sp,
                                            fontWeight = if (selectedTimeSlot == slot) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTimeSlot == slot) Color.White else Color(0xFF2D2D44),
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Duration Selection
                item {
                    AnimatedVisibility(visible = selectedTimeSlot != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Session Duration",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D2D44)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                durations.forEach { duration ->
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedDuration = duration },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (selectedDuration == duration) Color(0xFF3D3D5C) else Color(0xFFF3F4F6)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${duration}m",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (selectedDuration == duration) Color(0xFFB8E986) else Color(0xFF2D2D44)
                                            )
                                            Text(
                                                text = "$${(hourlyRate * duration / 60).toInt()}",
                                                fontSize = 12.sp,
                                                color = if (selectedDuration == duration) Color.White else Color(0xFF6B7280)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Description
                item {
                    AnimatedVisibility(visible = selectedDuration > 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Session Description",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D2D44)
                            )

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("What would you like to learn?") },
                                shape = RoundedCornerShape(12.dp),
                                minLines = 3
                            )
                        }
                    }
                }

                // Language
                item {
                    AnimatedVisibility(visible = description.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Language",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D2D44)
                            )

                            OutlinedTextField(
                                value = language,
                                onValueChange = { language = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("e.g., Spanish, French, etc.") },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                // Level Selection
                item {
                    AnimatedVisibility(visible = language.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Your Level",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D2D44)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                levels.forEach { lvl ->
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { level = lvl },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (level == lvl) Color(0xFF3D3D5C) else Color(0xFFF3F4F6)
                                    ) {
                                        Text(
                                            text = lvl.capitalize(Locale.ROOT),
                                            fontSize = 14.sp,
                                            fontWeight = if (level == lvl) FontWeight.Bold else FontWeight.Normal,
                                            color = if (level == lvl) Color.White else Color(0xFF2D2D44),
                                            modifier = Modifier.padding(12.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Summary and Book Button
                item {
                    AnimatedVisibility(
                        visible = selectedDate != null && selectedTimeSlot != null &&
                                description.isNotEmpty() && language.isNotEmpty()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Summary Card
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Booking Summary",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D2D44)
                                    )

                                    selectedDate?.let {
                                        Text(
                                            text = "${dateFormat.format(it)} at $selectedTimeSlot",
                                            fontSize = 13.sp,
                                            color = Color(0xFF6B7280)
                                        )
                                    }

                                    Text(
                                        text = "Duration: ${selectedDuration} minutes",
                                        fontSize = 13.sp,
                                        color = Color(0xFF6B7280)
                                    )

                                    Text(
                                        text = "Total: $${(hourlyRate * selectedDuration / 60).toInt()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D2D44)
                                    )
                                }
                            }

                            // Book Button
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            isLoading = true

                                            currentUser?.let { user ->
                                                // Create session request
                                                val sessionRequest = hashMapOf(
                                                    "studentId" to user.uid,
                                                    "trainerId" to trainerId,
                                                    "trainerName" to trainerName,
                                                    "date" to selectedDate!!,
                                                    "timeSlot" to selectedTimeSlot!!,
                                                    "duration" to selectedDuration,
                                                    "description" to description,
                                                    "language" to language,
                                                    "level" to level,
                                                    "status" to "pending",
                                                    "totalPrice" to (hourlyRate * selectedDuration / 60),
                                                    "createdAt" to System.currentTimeMillis(),
                                                    "updatedAt" to System.currentTimeMillis()
                                                )

                                                // Get student name
                                                val studentDoc = firestore.collection("users")
                                                    .document(user.uid)
                                                    .get()
                                                    .await()
                                                val studentName = studentDoc.getString("fullName") ?: "Student"

                                                sessionRequest["studentName"] = studentName

                                                // Add to Firestore
                                                firestore.collection("sessionRequests")
                                                    .add(sessionRequest)
                                                    .await()

                                                isLoading = false
                                                Toast.makeText(
                                                    context,
                                                    "Session request sent successfully!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                onDismiss()
                                            }
                                        } catch (e: Exception) {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Failed to send request: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFB8E986)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color(0xFF2D2D44)
                                    )
                                } else {
                                    Text(
                                        text = "Send Booking Request",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D2D44)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}