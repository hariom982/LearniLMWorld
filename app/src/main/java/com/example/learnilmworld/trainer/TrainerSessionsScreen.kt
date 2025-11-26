package com.example.learnilmworld.trainer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TrainerSessionsScreen() {
    // Sample sessions data
    val sessions = listOf(
        SessionItem(
            title = "Session 4",
            studentCount = "1 student(s)",
            dateTime = "Oct 13, 2025, 3:52 AM",
            description = "session description",
            duration = "60m",
            language = "Language",
            level = "intermediate",
            roomId = "admin-session-012a90d6-105a-4243-96ee-744787d8e19d",
            status = SessionStatus.COMPLETED
        ),
        SessionItem(
            title = "Session 3",
            studentCount = "1 student(s)",
            dateTime = "Oct 22, 2025, 11:47 AM",
            description = "3rd learning session",
            duration = "60m",
            language = "Language",
            level = "intermediate",
            roomId = "admin-session-1245ba69-fb1f-494e-908a-99feb8644515",
            status = SessionStatus.ACTIVE
        ),
        SessionItem(
            title = "Hindi session",
            studentCount = "1 student(s)",
            dateTime = "Dec 12, 2020, 12:12 PM",
            description = "Learn Hindi lang",
            duration = "60m",
            language = "Language",
            level = "intermediate",
            roomId = "admin-session-xyz123",
            status = SessionStatus.SCHEDULED
        )
    )

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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background( brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFD4A574),
                            Color(0xFFE6B87D)
                        )
                    ))
                    .padding(25.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Sessions",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D2D44)
                    )

                    Button(
                        onClick = { /* Create new session */ },
                        modifier = Modifier.height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3D3D5C)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create New Session",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Sessions List
            if (sessions.isEmpty()) {
                EmptySessionsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sessions.size) { index ->
                        SessionCard(session = sessions[index])
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

enum class SessionStatus {
    COMPLETED,
    ACTIVE,
    SCHEDULED
}

data class SessionItem(
    val title: String,
    val studentCount: String,
    val dateTime: String,
    val description: String,
    val duration: String,
    val language: String,
    val level: String,
    val roomId: String,
    val status: SessionStatus
)

@Composable
fun SessionCard(session: SessionItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Left side - Session info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Video icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFB8E986), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFF2D2D44),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Session details
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = session.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D44)
                        )

                        Text(
                            text = session.studentCount,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )

                        Text(
                            text = session.dateTime,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                // Right side - Status and actions
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (session.status) {
                            SessionStatus.COMPLETED -> Color(0xFFE5E7EB)
                            SessionStatus.ACTIVE -> Color(0xFFFEF3C7)
                            SessionStatus.SCHEDULED -> Color(0xFFDBEAFE)
                        }
                    ) {
                        Text(
                            text = when (session.status) {
                                SessionStatus.COMPLETED -> "COMPLETED"
                                SessionStatus.ACTIVE -> "ACTIVE"
                                SessionStatus.SCHEDULED -> "SCHEDULED"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (session.status) {
                                SessionStatus.COMPLETED -> Color(0xFF374151)
                                SessionStatus.ACTIVE -> Color(0xFF92400E)
                                SessionStatus.SCHEDULED -> Color(0xFF1E40AF)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    // Action buttons
                    when (session.status) {
                        SessionStatus.ACTIVE -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { /* Join session */ },
                                    modifier = Modifier.height(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3D3D5C)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Join",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFB8E986)
                                    )
                                }

                                Button(
                                    onClick = { /* End session */ },
                                    modifier = Modifier.height(30.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3D3D5C)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "End",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        SessionStatus.SCHEDULED -> {
                            Button(
                                onClick = { /* Start session */ },
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3D3D5C)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Start",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = session.description,
                fontSize = 15.sp,
                color = Color(0xFF4B5563),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Session details row
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Duration
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Duration: ${session.duration}",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Text(
                    text = "•",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )

                // Language
                Text(
                    text = "Language: ${session.language}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )

                Text(
                    text = "•",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )

                // Level
                Text(
                    text = "Level: ${session.level}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Room ID
            Text(
                text = "Room: ${session.roomId}",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun EmptySessionsState() {
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
                    .background(Color(0xFFE5E7EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(50.dp)
                )
            }

            Text(
                text = "No Sessions Yet",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2D44)
            )

            Text(
                text = "Create your first session to start teaching!",
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center
            )
        }
    }
}
