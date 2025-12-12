package com.example.learnilmworld.trainer

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class TrainerSessionRequest(
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
    val status: String = "pending",
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
fun TrainerSessionsScreen() {
    var sessionRequests by remember { mutableStateOf<List<TrainerSessionRequest>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val coroutineScope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Fetch session requests
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            currentUser?.let { user ->
                firestore.collection("sessionRequests")
                    .whereEqualTo("trainerId", user.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            return@addSnapshotListener
                        }

                        snapshot?.let {
                            sessionRequests = it.documents.mapNotNull { doc ->
                                TrainerSessionRequest(
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
                    }
            }
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Sessions",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D44)
                        )

                        // Pending requests badge
                        if (sessionRequests.count { it.status == "pending" } > 0) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEF4444)
                            ) {
                                Text(
                                    text = "${sessionRequests.count { it.status == "pending" }}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

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
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = title,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                        )

                                        // Badge for pending count
                                        if (index == 0 && sessionRequests.count { it.status == "pending" } > 0) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFEF4444)
                                            ) {
                                                Text(
                                                    text = "${sessionRequests.count { it.status == "pending" }}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
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
                    EmptyTrainerSessionsState(
                        message = when (selectedTab) {
                            0 -> "No pending requests"
                            1 -> "No confirmed sessions"
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
                            TrainerSessionCard(
                                session = session,
                                dateFormat = dateFormat,
                                onConfirm = {
                                    coroutineScope.launch {
                                        try {
                                            currentUser?.let { user ->
                                                // Get trainer name
                                                val trainerDoc = firestore.collection("users")
                                                    .document(user.uid)
                                                    .get()
                                                    .await()
                                                val trainerName = trainerDoc.getString("fullName") ?: "Trainer"

                                                // Create meeting session with Jitsi
                                                val meetingDetails = com.example.learnilmworld.utils.JitsiMeetingManager
                                                    .createMeetingSession(
                                                        sessionRequestId = session.id,
                                                        trainerName = trainerName,
                                                        studentName = session.studentName
                                                    )

                                                // Update session with room details
                                                firestore.collection("sessionRequests")
                                                    .document(session.id)
                                                    .update(
                                                        mapOf(
                                                            "status" to "confirmed",
                                                            "roomId" to meetingDetails.roomName,
                                                            "roomName" to meetingDetails.roomName,
                                                            "meetingLink" to meetingDetails.meetingLink,
                                                            "meetingType" to "JITSI",
                                                            "updatedAt" to System.currentTimeMillis()
                                                        )
                                                    )
                                                    .await()

                                                Toast.makeText(
                                                    context,
                                                    "Session confirmed! Meeting room created.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                context,
                                                "Failed to confirm: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onReject = {
                                    coroutineScope.launch {
                                        try {
                                            firestore.collection("sessionRequests")
                                                .document(session.id)
                                                .update(
                                                    mapOf(
                                                        "status" to "rejected",
                                                        "updatedAt" to System.currentTimeMillis()
                                                    )
                                                )
                                                .await()

                                            Toast.makeText(
                                                context,
                                                "Request rejected",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                context,
                                                "Failed to reject: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onJoinSession = {
                                    // Navigate to video call
                                },
                                onEndSession = {
                                    coroutineScope.launch {
                                        try {
                                            firestore.collection("sessionRequests")
                                                .document(session.id)
                                                .update(
                                                    mapOf(
                                                        "status" to "completed",
                                                        "updatedAt" to System.currentTimeMillis()
                                                    )
                                                )
                                                .await()

                                            Toast.makeText(
                                                context,
                                                "Session completed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                context,
                                                "Failed to end session: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
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
fun TrainerSessionCard(
    session: TrainerSessionRequest,
    dateFormat: SimpleDateFormat,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    onJoinSession: () -> Unit,
    onEndSession: () -> Unit
) {
    val context = LocalContext.current
    var showMeetingLinkDialog by remember { mutableStateOf(false) }

    if (showMeetingLinkDialog && session.meetingLink.isNotEmpty()) {
        MeetingLinkDialog(
            meetingLink = session.meetingLink,
            roomName = session.roomName,
            sessionTitle = "Session with ${session.studentName}",
            userName = session.trainerName,
            onDismiss = { showMeetingLinkDialog = false },
            onJoin = {
                // Launch native Jitsi Meet
                if (session.roomName.isNotEmpty()) {
                    com.example.learnilmworld.meeting.JitsiMeetingActivity.startMeeting(
                        context = context,
                        roomName = session.roomName,
                        userName = session.trainerName,
                        meetingLink = session.meetingLink
                    )
                } else {
                    Toast.makeText(context, "Meeting room not found", Toast.LENGTH_SHORT).show()
                }
            },
            onCopy = {
                com.example.learnilmworld.utils.JitsiMeetingManager.copyMeetingLink(
                    context = context,
                    meetingLink = session.meetingLink
                )
            },
            onShare = {
                com.example.learnilmworld.utils.JitsiMeetingManager.shareMeetingLink(
                    context = context,
                    meetingLink = session.meetingLink,
                    sessionTitle = "Session with ${session.studentName}"
                )
            }
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Student avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF8B7FD8), Color(0xFFA893E8))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = session.studentName,
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
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
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

            // Earnings
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFB8E986).copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "You'll Earn",
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
                "pending" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Reject",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                        }

                        Button(
                            onClick = onConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB8E986)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Confirm",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2D2D44)
                            )
                        }
                    }
                }
                "confirmed" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (session.meetingLink.isNotEmpty()) {
                                    showMeetingLinkDialog = true
                                } else {
                                    Toast.makeText(context, "Meeting link not available", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3D3D5C)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = Color(0xFFB8E986),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Join",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = onEndSession,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "End Session",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Room ID and Meeting Link
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
                                            com.example.learnilmworld.utils.JitsiMeetingManager.copyMeetingLink(
                                                context = context,
                                                meetingLink = session.meetingLink
                                            )
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
                                            com.example.learnilmworld.utils.JitsiMeetingManager.shareMeetingLink(
                                                context = context,
                                                meetingLink = session.meetingLink,
                                                sessionTitle = "Session with ${session.studentName}"
                                            )
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
fun EmptyTrainerSessionsState(message: String) {
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
fun MeetingLinkDialog(
    meetingLink: String,
    roomName: String,
    sessionTitle: String,
    userName: String,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onCopy()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                Color(0xFFE5E7EB)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Copy",
                                fontSize = 14.sp,
                                color = Color(0xFF2D2D44)
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onShare()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                Color(0xFFE5E7EB)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Share",
                                fontSize = 14.sp,
                                color = Color(0xFF2D2D44)
                            )
                        }
                    }
                }
            }
        }
    }
}