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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
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
fun TrainerStudentsScreen() {
    // Sample students data
    val students = listOf(
        StudentItem(
            name = "John",
            email = "user@email.com",
            bookingDate = "10/13/2025",
            amount = "$25",
            status = "Session Created"
        ),
        StudentItem(
            name = "Sarah Miller",
            email = "sarah.miller@email.com",
            bookingDate = "10/15/2025",
            amount = "$25",
            status = "Completed"
        ),
        StudentItem(
            name = "Michael Chen",
            email = "michael.chen@email.com",
            bookingDate = "10/18/2025",
            amount = "$25",
            status = "Upcoming"
        ),
        StudentItem(
            name = "Emma Rodriguez",
            email = "emma.r@email.com",
            bookingDate = "10/20/2025",
            amount = "$25",
            status = "Session Created"
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
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFD4A574)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Students",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D2D44)
                    )

                    Button(
                        onClick = { /* Create session */ },
                        modifier = Modifier.height(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3D3D5C)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color(0xFFB8E986),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Create Session",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB8E986)
                        )
                    }
                }
            }

            // Students List
            if (students.isEmpty()) {
                EmptyStudentsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(students.size) { index ->
                        StudentCard(student = students[index])
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

data class StudentItem(
    val name: String,
    val email: String,
    val bookingDate: String,
    val amount: String,
    val status: String
)

@Composable
fun StudentCard(student: StudentItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
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
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Left side - Student info
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color(0xFF8B7FD8), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Student details
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = student.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D44)
                    )

                    Text(
                        text = student.email,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Booked on ${student.bookingDate}",
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF)
                        )

                        Text(
                            text = "•",
                            fontSize = 13.sp,
                            color = Color(0xFF9CA3AF)
                        )

                        Text(
                            text = student.amount,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF10B981)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (student.status) {
                            "Session Created" -> Color(0xFFDEDEF8)
                            "Completed" -> Color(0xFFD1FAE5)
                            "Upcoming" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFF3F4F6)
                        }
                    ) {
                        Text(
                            text = student.status,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (student.status) {
                                "Session Created" -> Color(0xFF4C1D95)
                                "Completed" -> Color(0xFF065F46)
                                "Upcoming" -> Color(0xFF92400E)
                                else -> Color(0xFF374151)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStudentsState() {
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
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Text(
                text = "No Students Yet",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Your enrolled students will appear here once they book sessions with you.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}
