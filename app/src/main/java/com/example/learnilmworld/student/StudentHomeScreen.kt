package com.example.learnilmworld.student

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnilmworld.screen.ActionCard
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.navigation.NavController
import com.example.learnilmworld.screen.CourseSlide
import com.example.learnilmworld.screen.LanguageCard
import com.example.learnilmworld.screen.NewsUpdate
import com.example.learnilmworld.screen.SubjectsCard
import com.example.learnilmworld.screen.UpskillAddon
import com.example.learnilmworld.screen.WisdomCard
import com.example.learnilmworld.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudentHomeScreen(viewModel: AuthViewModel,navController: NavController) {

    val currentUser by viewModel.currentUser.collectAsState()


    val courseSlides = listOf(
        CourseSlide(
            title = "Advanced Mathematics",
            description = "Master calculus, algebra, and geometry with expert guidance",
            backgroundColor = Color(0xFF6366F1),
            icon = "📐",
            destination = "course_math"
        ),
        CourseSlide(
            title = "Digital Marketing",
            description = "Learn SEO, social media marketing, and content strategy",
            backgroundColor = Color(0xFFEC4899),
            icon = "📱",
            destination = "course_marketing"
        ),
        CourseSlide(
            title = "Data Science Fundamentals",
            description = "Python, statistics, and machine learning basics",
            backgroundColor = Color(0xFF8B5CF6),
            icon = "📊",
            destination = "course_datascience"
        ),
        CourseSlide(
            title = "Creative Writing",
            description = "Develop your storytelling and writing skills",
            backgroundColor = Color(0xFF10B981),
            icon = "✍️",
            destination = "course_writing"
        )
    )

    val languages = listOf(
        LanguageCard(
            flag = "🇬🇧",
            language = "English",
            description = "Master the global language of business and communication",
            backgroundColor = Color(0xFF3B82F6)
        ),
        LanguageCard(
            flag = "🇪🇸",
            language = "Spanish",
            description = "Learn the second most spoken language worldwide",
            backgroundColor = Color(0xFFEF4444)
        ),
        LanguageCard(
            flag = "🇫🇷",
            language = "French",
            description = "Discover the language of art, culture and diplomacy",
            backgroundColor = Color(0xFF8B5CF6)
        ),
        LanguageCard(
            flag = "🇩🇪",
            language = "German",
            description = "Unlock opportunities in Europe's largest economy",
            backgroundColor = Color(0xFF10B981)
        ),
        LanguageCard(
            flag = "🇨🇳",
            language = "Mandarin",
            description = "Connect with the world's most spoken language",
            backgroundColor = Color(0xFFFF6B35)
        ),
        LanguageCard(
            flag = "🇯🇵",
            language = "Japanese",
            description = "Explore the language of innovation and tradition",
            backgroundColor = Color(0xFFA855F7)
        )
    )

    val actionCards = listOf(
        ActionCard(
            icon = "👥",
            iconColor = Color(0xFF10B981),
            title = "Find Your Perfect Trainer",
            description = "Browse through our network of expert trainers and counselors to find the perfect match for your learning goals.",
            buttonText = "Browse All Trainers",
            buttonColor = Color(0xFF10B981),
            destination = "browse_trainers/"
        ),
//        ActionCard(
//            icon = "💬",
//            iconColor = Color(0xFF3B82F6),
//            title = "Your Learning Journey",
//            description = "Keep track of your sessions, view feedback from trainers, and monitor your progress.",
//            buttonText = "View My Sessions",
//            buttonColor = Color(0xFF3B82F6),
//            destination = "sessions"
//        )
    )

    //upskill addon
    val upskillAddons = listOf(
        UpskillAddon(
            icon = "🎥",
            title = "Video Editing",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFE3F2FD)
        ),
        UpskillAddon(
            icon = "✍️",
            title = "Content Creation",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFE8F5E9)
        ),
        UpskillAddon(
            icon = "💼",
            title = "Career Guidance",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFFFF3E0)
        ),
        UpskillAddon(
            icon = "📚",
            title = "Study Groups",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFF3E5F5)
        )
    )

 //Subjects Card items
    val subjects = listOf(
        SubjectsCard(
            icon = "📐",
            title = "Mathematics",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFE3F2FD)
        ),
        SubjectsCard(
            icon = "⚛️",
            title = "Physics",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFE8F5E9)
        ),
        SubjectsCard(
            icon = "🧪",
            title = "Chemistry",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFFFF3E0)
        ),
        SubjectsCard(
            icon = "🏛️",
            title = "Political Science",
            subtitle = "Mentors available",
            backgroundColor = Color(0xFFF3E5F5)
        )
    )

    val wisdomCards = listOf(
        WisdomCard(
            icon = "💡",
            title = "Gita Insights",
            subtitle = "Available",
            iconColor = Color(0xFFFF6B35),
            btntext = "Read"
        ),
        WisdomCard(
            icon = "🧘",
            title = "Mindful Meditation",
            subtitle = "Available",
            iconColor = Color(0xFF3B82F6),
            btntext = "Listen"
        ),
        WisdomCard(
            icon = "📖",
            title = "Ramayana",
            subtitle = "Available",
            iconColor = Color(0xFF10B981),
            btntext = "Read"
        ),
        WisdomCard(
            icon = "📖",
            title = "Bible",
            subtitle = "Available",
            iconColor = Color(0xFFA855F7),
            btntext = "Read"
        )
    )

    val newsUpdates = listOf(
        NewsUpdate(
            image = "🤖",
            title = "New AI-powered speaking practice launched!",
            description = "Our latest feature revolutionizes language learning. Try it now!",
            date = "2h ago",
            backgroundColor = Color(0xFFFFF3E0)
        ),
        NewsUpdate(
            image = "🌍",
            title = "Global Languages Summit highlights key trends",
            description = "Learn about the future of learning from industry experts.",
            date = "1d ago",
            backgroundColor = Color(0xFFE3F2FD)
        ),
        NewsUpdate(
            image = "📊",
            title = "Find out the latest stats of the moment, Mentor",
            description = "Insights into global language acquisition and success.",
            date = "3d ago",
            backgroundColor = Color(0xFFE8F5E9)
        ),
        NewsUpdate(
            image = "🎯",
            title = "New certification programs available",
            description = "Advance your career with industry-recognized certificates.",
            date = "5d ago",
            backgroundColor = Color(0xFFF3E5F5)
        )
    )



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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            item {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Hii, "+currentUser?.fullName ?: "Student",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
//                    Text(
//                        text = "ready to speak like a native today?",
//                        fontSize = 15.sp,
//                        fontFamily = FontFamily.Serif,
//                        fontWeight = FontWeight.Normal,
//                        color = Color.White
//                    )
                }
            }

            // Auto Image Slider
            item {
                AutoImageSlider(
                    slides = courseSlides,
                    navController = navController
                )
            }
            item {
                Spacer(modifier = Modifier.size(8.dp))
            }
            item{
                Text(
                    text = "ready to speak like a native today?",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            }
            // Language Cards Grid
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    languages.chunked(2).forEach { rowLanguages ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowLanguages.forEach { language ->
                                Box(modifier = Modifier.weight(1f)) {
                                    LanguageCardItem(
                                        language = language,
                                        navController = navController
                                    )
                                }
                            }
                            // Add empty space if odd number of items in last row
                            if (rowLanguages.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Upskill Add-ons Section
            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Upskill (Add-ons)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(upskillAddons) { addon ->
                            UpskillAddonCard(addon = addon)
                        }
                    }
                }
            }
            // Subjects Section
            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Learn Subjects",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(subjects) { card ->
                            SubjectsCardSection(card = card)
                        }
                    }
                }
            }

            // Wisdom Section
            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Wisdom (5-min Daily)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(wisdomCards) { card ->
                            WisdomCardItem(card = card)
                        }
                    }
                }
            }

            // Action Cards
            items(actionCards) { actionCard ->
                ActionCardItem(actionCard = actionCard, navController = navController)
            }

            // News & Updates Section
            item {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "News & Updates",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "View All Updates →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.clickable {
                                // Navigate to all updates
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        newsUpdates.forEach { news ->
                            NewsUpdateCard(news = news)
                        }
                    }
                }
            }

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoImageSlider(
    slides: List<CourseSlide>,
    navController: NavController,
    autoScrollDuration: Long = 3000L
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-scroll effect
    LaunchedEffect(pagerState) {
        launch {
            while (true) {
                delay(autoScrollDuration)
                val nextPage = (pagerState.currentPage + 1) % slides.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { page ->
            CourseSlideItem(
                slide = slides[page],
                navController = navController
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Page Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(slides.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

@Composable
fun CourseSlideItem(
    slide: CourseSlide,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = slide.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            slide.backgroundColor,
                            slide.backgroundColor.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slide.icon,
                            fontSize = 32.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = slide.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = slide.description,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Explore Button
                Button(
                    onClick = {
                        navController.navigate(slide.destination)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "Explore Course",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = slide.backgroundColor
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageCardItem(language: LanguageCard, navController: NavController) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .scale(scale)
            .clickable {
                isPressed = !isPressed
                // Navigate to browse trainers with language filter
                navController.navigate("browse_trainers/${language.language}")
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Flag Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                ,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = language.flag,
                    fontSize = 40.sp
                )
            }

            // Language and Description
            Column {
                Text(
                    text = language.language,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF2D2D44),
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = language.description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun ActionCardItem(actionCard: ActionCard, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
//                    .shadow(8.dp, RoundedCornerShape(14.dp))
//                    .background(actionCard.iconColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    ,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionCard.icon,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = actionCard.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = Color(0xFF2D2D44)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = actionCard.description,
                fontSize = 15.sp,
                color = Color(0xFF6B7280),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(
                onClick = {
                    navController.navigate(actionCard.destination)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = actionCard.buttonColor
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 10.dp
                )
            ) {
                Text(
                    text = actionCard.buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun UpskillAddonCard(addon: UpskillAddon) {
    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = addon.backgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = addon.icon,
                    fontSize = 28.sp
                )
            }

            // Text Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = addon.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = addon.subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Explore Button
            Button(
                onClick = { /* Handle click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Explore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}
@Composable
fun SubjectsCardSection(card: SubjectsCard) {
    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = card.backgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.icon,
                    fontSize = 28.sp
                )
            }

            // Text Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = card.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = card.subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Explore Button
            Button(
                onClick = { /* Handle click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Explore",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun WisdomCardItem(card: WisdomCard) {
    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.icon,
                    fontSize = 28.sp
                )
            }

            // Text Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = card.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D44),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = card.subtitle,
                        fontSize = 11.sp,
                        color = Color.Green,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Read/Action Button
            Button(
                onClick = { /* Handle click */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = card.btntext,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun NewsUpdateCard(news: NewsUpdate) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image/Icon Box
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = news.backgroundColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = news.image,
                    fontSize = 32.sp
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = news.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D44),
                        lineHeight = 20.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = news.description,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        lineHeight = 18.sp,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = news.date,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}