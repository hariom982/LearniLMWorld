package com.example.learnilmworld.student

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class SessionRequest(
    val id: String = "",
    val studentId: String = "",
    val trainerId: String = "",
    val trainerName: String = "",
    val studentName: String = "",
    val date: Date? = null,
    val timeSlot: String = "",
    val duration: Int = 0,
    val description: String = "",
    val language: String = "",
    val level: String = "",
    val status: String = "pending", // pending, confirmed, rejected, completed
    val totalPrice: Double = 0.0,
    val roomId: String = "",
    val roomName: String = "",
    val meetingLink: String = "",
    val meetingType: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen() {
    var sessionRequests by remember { mutableStateOf<List<SessionRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Fetch session requests
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            currentUser?.let { user ->
                val snapshot = firestore.collection("sessionRequests")
                    .whereEqualTo("studentId", user.uid)
                    .get()
                    .await()

                sessionRequests = snapshot.documents.mapNotNull { doc ->
                    SessionRequest(
                        id = doc.id,
                        studentId = doc.getString("studentId") ?: "",
                        trainerId = doc.getString("trainerId") ?: "",
                        trainerName = doc.getString("trainerName") ?: "",
                        studentName = doc.getString("studentName") ?: "",
                        date = doc.getDate("date"),
                        timeSlot = doc.getString("timeSlot") ?: "",
                        duration = doc.getLong("duration")?.toInt() ?: 0,
                        description = doc.getString("description") ?: "",
                        language = doc.getString("language") ?: "",
                        level = doc.getString("level") ?: "",
                        status = doc.getString("status") ?: "pending",
                        totalPrice = doc.getDouble("totalPrice") ?: 0.0,
                        roomId = doc.getString("roomId") ?: "",
                        roomName = doc.getString("roomName") ?: "",
                        meetingLink = doc.getString("meetingLink") ?: "",
                        meetingType = doc.getString("meetingType") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        updatedAt = doc.getLong("updatedAt") ?: 0L
                    )
                }.sortedByDescending { it.createdAt }
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
            Toast.makeText(context, "Failed to load sessions: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    val tabs = listOf("Pending", "Confirmed", "Completed", "Rejected")
    val filteredSessions = remember(sessionRequests, selectedTab) {
        when (selectedTab) {
            0 -> sessionRequests.filter { it.status == "pending" }
            1 -> sessionRequests.filter { it.status == "confirmed" }
            2 -> sessionRequests.filter { it.status == "completed" }
            3 -> sessionRequests.filter { it.status == "rejected" }
            else -> sessionRequests
        }
    }

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
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFD4A574),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "My Sessions",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D44)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tabs
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF2D2D44),
                        edgePadding = 0.dp,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = Color(0xFF2D2D44),
                                    height = 3.dp
                                )
                            }
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Content
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                filteredSessions.isEmpty() -> {
                    EmptySessionsState(
                        message = when (selectedTab) {
                            0 -> "No pending session requests"
                            1 -> "No confirmed sessions yet"
                            2 -> "No completed sessions"
                            3 -> "No rejected requests"
                            else -> "No sessions found"
                        }
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredSessions) { session ->
                            StudentSessionCard(
                                session = session,
                                dateFormat = dateFormat,
                                onJoinSession = {
                                    // Navigate to video call
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentSessionCard(
    session: SessionRequest,
    dateFormat: SimpleDateFormat,
    onJoinSession: () -> Unit
) {
    val context = LocalContext.current
    var showMeetingLinkDialog by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    if (showMeetingLinkDialog && session.meetingLink.isNotEmpty()) {
        MeetingLinkDialogStudent(
            meetingLink = session.meetingLink,
            roomName = session.roomName,
            sessionTitle = "Session with ${session.trainerName}",
            userName = session.studentName,
            onDismiss = { showMeetingLinkDialog = false },
            onJoin = {
                if (session.roomName.isNotEmpty()) {
                    com.example.learnilmworld.VideoCallActivity.startMeeting(
                        context = context,
                        callID = session.id,           // Use your sessionRequest.id as the unique call/room ID
                        userID = session.studentId,      // Or trainerId/studentId depending on who is joining
                        userName = session.studentName     // trainerName or studentName
                    )
                } else {
                    Toast.makeText(context, "Meeting room not found", Toast.LENGTH_SHORT).show()
                }
            },
//            onCopy = {
//                com.example.learnilmworld.utils.JitsiMeetingManager.copyMeetingLink(
//                    context = context,
//                    meetingLink = session.meetingLink
//                )
//            },
//            onShare = {
//                com.example.learnilmworld.utils.JitsiMeetingManager.shareMeetingLink(
//                    context = context,
//                    meetingLink = session.meetingLink,
//                    sessionTitle = "Session with ${session.trainerName}"
//                )
//            }
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Session with ${session.trainerName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D44)
                    )

                    session.date?.let {
                        Text(
                            text = "${dateFormat.format(it)} at ${session.timeSlot}",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (session.status) {
                        "pending" -> Color(0xFFFEF3C7)
                        "confirmed" -> Color(0xFFDBEAFE)
                        "completed" -> Color(0xFFE5E7EB)
                        "rejected" -> Color(0xFFFFE5E5)
                        else -> Color(0xFFF3F4F6)
                    }
                ) {
                    Text(
                        text = session.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (session.status) {
                            "pending" -> Color(0xFF92400E)
                            "confirmed" -> Color(0xFF1E40AF)
                            "completed" -> Color(0xFF374151)
                            "rejected" -> Color(0xFFEF4444)
                            else -> Color(0xFF6B7280)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Description
            Text(
                text = session.description,
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = 20.sp
            )

            // Session Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Duration
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${session.duration}m",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // Language
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = session.language,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // Level
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = session.level.capitalize(Locale.ROOT),
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            // Price
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF3F4F6)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Amount",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = "$${session.totalPrice.toInt()}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D44)
                    )
                }
            }

            // Action Buttons
            when (session.status) {
                "confirmed" -> {
                    Button(
                        onClick = {
                            com.example.learnilmworld.VideoCallActivity.startMeeting(  // or VideoCallActivity if you renamed it
                                context = context,
                                callID = session.id,
                                userID = session.studentId,
                                userName = session.studentName  // or fetch from Firestore if needed
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB8E986)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFF2D2D44),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join Session",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D44)
                        )
                    }
                }
                "pending" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Waiting for trainer confirmation...",
                            fontSize = 14.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                "rejected" -> {
                    Text(
                        text = "This session request was declined by the trainer.",
                        fontSize = 14.sp,
                        color = Color(0xFFEF4444),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Room ID for confirmed sessions
            if (session.status == "confirmed") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (session.roomId.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDBEAFE).copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Room ID: ${session.roomId}",
                                fontSize = 12.sp,
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    if (session.meetingLink.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFB8E986).copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Meeting Link Ready",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2D2D44)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = {
//                                            com.example.learnilmworld.utils.JitsiMeetingManager.copyMeetingLink(
//                                                context = context,
//                                                meetingLink = session.meetingLink
//                                            )
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy",
                                            tint = Color(0xFF2D2D44),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
//                                            com.example.learnilmworld.utils.JitsiMeetingManager.shareMeetingLink(
//                                                context = context,
//                                                meetingLink = session.meetingLink,
//                                                sessionTitle = "Session with ${session.trainerName}"
//                                            )
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share",
                                            tint = Color(0xFF2D2D44),
                                            modifier = Modifier.size(16.dp)
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
}

@Composable
fun EmptySessionsState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Text(
                text = "No Sessions",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MeetingLinkDialogStudent(
    meetingLink: String,
    roomName: String,
    sessionTitle: String,
    userName: String,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
//    onCopy: () -> Unit,
//    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Join Meeting",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D44)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF6B7280)
                        )
                    }
                }

                // Meeting icon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF3D3D5C), Color(0xFF2D2D44))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFFB8E986),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Text(
                    text = sessionTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Meeting link display
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = meetingLink,
                        fontSize = 13.sp,
                        color = Color(0xFF2D2D44),
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 20.sp
                    )
                }

                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Join button
                    Button(
                        onClick = {
                            onJoin()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB8E986)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFF2D2D44),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Join Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D44)
                        )
                    }

                    // Secondary actions
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        OutlinedButton(
//                            onClick = {
//                                onCopy()
//                            },
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(48.dp),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                containerColor = Color.Transparent
//                            ),
//                            border = androidx.compose.foundation.BorderStroke(
//                                1.5.dp,
//                                Color(0xFFE5E7EB)
//                            ),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.ContentCopy,
//                                contentDescription = null,
//                                tint = Color(0xFF6B7280),
//                                modifier = Modifier.size(20.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = "Copy",
//                                fontSize = 14.sp,
//                                color = Color(0xFF2D2D44)
//                            )
//                        }
//
//                        OutlinedButton(
//                            onClick = {
//                                onShare()
//                            },
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(48.dp),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                containerColor = Color.Transparent
//                            ),
//                            border = androidx.compose.foundation.BorderStroke(
//                                1.5.dp,
//                                Color(0xFFE5E7EB)
//                            ),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Share,
//                                contentDescription = null,
//                                tint = Color(0xFF6B7280),
//                                modifier = Modifier.size(20.dp)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            Text(
//                                text = "Share",
//                                fontSize = 14.sp,
//                                color = Color(0xFF2D2D44)
//                            )
//                        }
//                    }
                }
            }
        }
    }
}