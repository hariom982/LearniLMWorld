package com.example.learnilmworld.student

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.learnilmworld.BookingDialog
import com.example.learnilmworld.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseTrainersScreen(navController: NavController,
                         selectedLanguage: String? = null) {
    var searchQuery by remember { mutableStateOf(selectedLanguage ?: "") }
    var selectedFilter by remember { mutableStateOf("All") }
    var trainers by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    var showProfileDialog by remember { mutableStateOf(false) }
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedTrainer by remember { mutableStateOf<User?>(null) }
    var favoriteTrainerIds by remember { mutableStateOf<Set<String>>(emptySet()) }


    // Fetch trainers from Firestore
    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val snapshot = firestore.collection("users")
                .whereEqualTo("userType", "TRAINER")
                .get()
                .await()

            trainers = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Failed to load trainers: ${e.message}"
            isLoading = false
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    // Filter trainers based on search query
    val filteredTrainers = remember(trainers, searchQuery, selectedFilter) {
        trainers.filter { trainer ->
            val matchesSearch = searchQuery.isBlank() ||
                    trainer.fullName.contains(searchQuery, ignoreCase = true) ||
                    trainer.languagesToTeach.any { it.contains(searchQuery, ignoreCase = true) } ||
                    trainer.specializations.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesFilter = when (selectedFilter) {
                "All" -> true
                "Favorites" -> favoriteTrainerIds.contains(trainer.uid)
                "Top Rated" -> trainer.averageRating >= 4.5
                "Experienced" -> trainer.yearsOfExperience >= 10
                "Available" -> trainer.isAvailableForBookings
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    val filterOptions = listOf("All","Favorites", "Top Rated", "Experienced", "Available")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3F51B5),
                        Color(0xFF6073E3),
                        Color(0xFFFFF5E1)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with search
            Surface(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3F51B5),
                                Color(0xFF6073E3),
                            )
                        )
                    ),
                color = Color.Transparent,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, bottom = 15.dp)
                        .windowInsetsPadding(
                            WindowInsets.statusBars.only(WindowInsetsSides.Top)
                        )
                ) {
                    // Back button and title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            Text(
                                text = "Browse Trainers",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(20.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        placeholder = {
                            LazyRow{
                                item {  Text(
                                    "Search by name, language, or specialization...",
                                    fontSize = 14.sp,
                                    color = Color(0xFF9CA3AF)
                                )}
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF6B7280)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color(0xFF6B7280)
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filter chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filterOptions) { filter ->
                            FilterChip(
                                text = filter,
                                isSelected = selectedFilter == filter,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                }
            }

            // Results count
            if (!isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredTrainers.size} trainers found",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    if (searchQuery.isNotEmpty() || selectedFilter != "All") {
                        TextButton(
                            onClick = {
                                searchQuery = ""
                                selectedFilter = "All"
                            }
                        ) {
                            Text(
                                "Clear filters",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
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
                errorMessage != null -> {
                    EmptyState(
                        icon = Icons.Default.Error,
                        title = "Error Loading Trainers",
                        message = errorMessage ?: "Something went wrong"
                    )
                }
                filteredTrainers.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.SearchOff,
                        title = "No trainers found",
                        message = if (searchQuery.isNotEmpty()) {
                            "Try adjusting your search or filters"
                        } else {
                            "No trainers available at the moment"
                        }
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredTrainers) { trainer ->
                            TrainerCard(
                                trainer = trainer,
                                onCardClick = {
                                    selectedTrainer = trainer
                                    showProfileDialog = true
                                },
                                onBookSession = {
                                    selectedTrainer = trainer
                                    showBookingDialog = true
                                },
                                isFavorite = favoriteTrainerIds.contains(trainer.uid),
                                onToggleFavorite = {
                                    favoriteTrainerIds = if (favoriteTrainerIds.contains(trainer.uid)) {
                                        favoriteTrainerIds - trainer.uid
                                    } else {
                                        favoriteTrainerIds + trainer.uid
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

        //Booking dialog
        if (showBookingDialog && selectedTrainer != null) {
            BookingDialog(
                trainerName = selectedTrainer!!.fullName,
                trainerId = selectedTrainer!!.uid,
                hourlyRate = selectedTrainer!!.hourlyRate,
                onDismiss = {
                    showBookingDialog = false
                    selectedTrainer = null
                }
            )
        }
        // Profile Dialog
        if (showProfileDialog && selectedTrainer != null) {
            TrainerProfileDialog(
                trainer = selectedTrainer!!,
                onDismiss = {
                    showProfileDialog = false
                },
                onBookSession = {
                    showProfileDialog = false
                    showBookingDialog = true
                },
                isFavorite = favoriteTrainerIds.contains(selectedTrainer!!.uid),
                onToggleFavorite = {
                    favoriteTrainerIds = if (favoriteTrainerIds.contains(selectedTrainer!!.uid)) {
                        favoriteTrainerIds - selectedTrainer!!.uid
                    } else {
                        favoriteTrainerIds + selectedTrainer!!.uid
                    }
                }
            )
        }
    }
}

@Composable
fun TrainerProfileDialog(
    trainer: User,
    onDismiss: () -> Unit,
    onBookSession: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with gradient background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF8B7FD8),
                                    Color(0xFFA893E8)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = onToggleFavorite,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (isFavorite) Color(0xFFEF4444) else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Profile Image
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.White
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = trainer.fullName,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB020),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = String.format("%.1f", trainer.averageRating),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "(0 reviews)",
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "$${trainer.hourlyRate.toInt()}/hour",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Quick Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            icon = Icons.Default.AccessTime,
                            value = "${trainer.yearsOfExperience}+",
                            label = "Years Exp"
                        )
                        StatItem(
                            icon = Icons.Default.Language,
                            value = "${trainer.languagesToTeach.size}",
                            label = "Languages"
                        )
                        StatItem(
                            icon = Icons.Default.CheckCircle,
                            value = if (trainer.isAvailableForBookings) "Available" else "Busy",
                            label = "Status"
                        )
                    }

                    Divider(color = Color(0xFFE5E7EB))

                    // Bio Section
                    if (trainer.bio.isNotEmpty()) {
                        SectionHeader(
                            icon = Icons.Default.Info,
                            title = "About Me"
                        )
                        Text(
                            text = trainer.bio,
                            fontSize = 15.sp,
                            color = Color(0xFF6B7280),
                            lineHeight = 22.sp
                        )
                    }

                    // Languages Section
                    SectionHeader(
                        icon = Icons.Default.Language,
                        title = "Languages I Teach"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        trainer.languagesToTeach.forEach { language ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.1f),
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Text(
                                    text = language,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    // Specializations Section
                    if (trainer.specializations.isNotEmpty()) {
                        SectionHeader(
                            icon = Icons.Default.Star,
                            title = "Specializations"
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            trainer.specializations.forEach { spec ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFFA855F7), CircleShape)
                                    )
                                    Text(
                                        text = spec,
                                        fontSize = 15.sp,
                                        color = Color(0xFF2D2D44)
                                    )
                                }
                            }
                        }
                    }

                    // Location
                    SectionHeader(
                        icon = Icons.Default.LocationOn,
                        title = "Teaching Mode"
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3F4F6)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = Color(0xFF6B7280)
                            )
                            Text(
                                text = "Online Sessions via Video Call",
                                fontSize = 15.sp,
                                color = Color(0xFF2D2D44)
                            )
                        }
                    }
                }

                // Action Buttons
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
//                        OutlinedButton(
//                            onClick = onViewFullProfile,
//                            modifier = Modifier
//                                .weight(1f)
//                                .height(50.dp),
//                            shape = RoundedCornerShape(12.dp),
//                            colors = ButtonDefaults.outlinedButtonColors(
//                                contentColor = Color(0xFFA855F7)
//                            ),
//                            border = androidx.compose.foundation.BorderStroke(
//                                2.dp,
//                                Color(0xFFA855F7)
//                            )
//                        ) {
//                            Text(
//                                text = "Full Profile",
//                                fontSize = 15.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }

                        Button(
                            onClick = onBookSession,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB8E986)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color(0xFF2D2D44)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Book Session",
                                fontSize = 15.sp,
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

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFA855F7),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D44)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFA855F7),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D44)
        )
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
        modifier = Modifier.shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2D2D44) else Color.White
        )
    }
}

@Composable
fun TrainerCard(
    trainer: User,
    onCardClick: () -> Unit,
    onBookSession:() -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    var isLiked by remember { mutableStateOf(false) }
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedTrainer by remember { mutableStateOf<User?>(null) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .clickable { onCardClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF8B7FD8),
                                    Color(0xFFA893E8)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (trainer.profileImageUrl.isNotEmpty()) {
                        // Load image from URL
                        // AsyncImage(model = trainer.profileImageUrl, contentDescription = "Profile")
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }
                }

                // Trainer info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Name and rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = trainer.fullName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D44),
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFA855F7).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$${trainer.hourlyRate.toInt()}/hr",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA855F7),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB020),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = String.format("%.1f", trainer.averageRating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2D2D44)
                        )
                        Text(
                            text = "(0 reviews)",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Languages
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Languages I Teach",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280)
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trainer.languagesToTeach.take(3)) { language ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF3F4F6)
                        ) {
                            Text(
                                text = language,
                                fontSize = 13.sp,
                                color = Color(0xFF2D2D44),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (trainer.languagesToTeach.size > 3) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Text(
                                    text = "+${trainer.languagesToTeach.size - 3}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF6B7280),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            if (trainer.bio.isNotEmpty()) {
                Text(
                    text = trainer.bio,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 20.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Experience and availability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${trainer.yearsOfExperience}+ years",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Online",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .weight(0.3f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isFavorite) Color(0xFFFFE5E5) else Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isFavorite) Color(0xFFEF4444) else Color(0xFFE5E7EB)
                    )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Button(
                    onClick = onBookSession,
                    modifier = Modifier
                        .weight(0.7f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB8E986)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "Book Session",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D44)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.SearchOff,
    title: String,
    message: String
) {
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}