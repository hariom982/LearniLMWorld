package com.example.learnilmworld

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.learnilmworld.viewModel.AuthViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

data class OnboardingPage(
    val emoji: String?,
    val title: String,
    val description: String,
    val floatingEmojis: List<String>
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val viewModel: AuthViewModel = viewModel()
    val navController = rememberNavController()
    val authState by viewModel.authState.collectAsState()
//    val currentUser by viewModel.currentUser.collectAsState()
//    // Auto-navigate on app start if user is logged in
//
//        LaunchedEffect(currentUser) {
//            currentUser?.let { user ->
//                val route = if (user.userType == "STUDENT") {
//                    "student_home"
//                } else {
//                    "trainer_home"
//                }
//                navController.navigate(route) {
//                    popUpTo(0) { inclusive = true }
//                }
//            }
//        }

    val pages = listOf(
        OnboardingPage(
            emoji = "🌎",
            title = "Learn Languages\nFrom Anywhere",
            description = "Connect with native speakers and expert trainers from around the world to master any language.",
            floatingEmojis = listOf("🇪🇸", "🇫🇷", "🇩🇪", "🇯🇵")
        ),
        OnboardingPage(
            emoji = "🌎",
            title = "Expert Native\nTrainers",
            description = "Learn from certified language experts who bring authentic cultural insights to every lesson.",
            floatingEmojis = listOf("💬", "📚", "🎯", "⭐")
        ),
        OnboardingPage(
            emoji = "🌎",
            title = "Interactive\nLearning Tools",
            description = "Engage with videos, quizzes, live sessions, and personalized practice to accelerate your progress.",
            floatingEmojis = listOf("🎥", "🎧", "📝", "🏆")
        ),
        OnboardingPage(
            emoji = "🌎",
            title = "Start Your\nJourney Today",
            description = "Join thousands of learners achieving fluency. Your language adventure begins now!",
            floatingEmojis = listOf("✨", "🎊", "🌟", "💫")
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFFf093fb)
                    )
                )
            )
    ) {
        // Animated particles background
        FloatingParticles()

        // App name at top
        AppNameHeader()

        // Skip button
//        TextButton(
//            onClick = {navController.navigate("choicescreen")},
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(top = 40.dp, end = 24.dp)
//        ) {
//            Text(
//                text = "Skip",
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//        }

        // Pager content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    isVisible = pagerState.currentPage == page
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Page indicators
            PageIndicators(
                pageCount = pages.size,
                currentPage = pagerState.currentPage,
                onPageClick = { page ->
                    scope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Navigation buttons
            NavigationButtons(
                currentPage = pagerState.currentPage,
                totalPages = pages.size,
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                onBack = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onGetStarted = onFinish
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun AppNameHeader() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -100 },
            animationSpec = tween(1000, easing = EaseOutCubic)
        ) + fadeIn(animationSpec = tween(1000))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 60.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌍 LearniLMWorld",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun FloatingParticles() {
    val particles = remember { List(15) { index -> index } }

    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { index ->
            FloatingParticle(index = index)
        }
    }
}

@Composable
fun FloatingParticle(index: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "particle_$index")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = tween((10000 + index * 500), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (index % 2 == 0) 50f else -50f,
        animationSpec = infiniteRepeatable(
            animation = tween((8000 + index * 300), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween((5000 + index * 200), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val size = (30 + index * 5).dp

    Box(
        modifier = Modifier
            .offset(
                x = (index * 60).dp + offsetX.dp,
                y = (index * 40).dp + offsetY.dp
            )
            .size(size)
            .alpha(alpha)
            .background(Color.White, CircleShape)
    )
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    isVisible: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale)
            .alpha(alpha)
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration with floating emojis
        Box(
            modifier = Modifier.size(280.dp),
            contentAlignment = Alignment.Center
        ) {
            // Main emoji circle with pulse animation
            PulsingIllustration(emoji = page.emoji.toString(), isVisible = isVisible)

            // Floating emojis around
            FloatingEmojis(emojis = page.floatingEmojis, isVisible = isVisible)
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 44.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun PulsingIllustration(emoji: String, isVisible: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_ring"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulse ring
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(pulseScale)
                .alpha(pulseAlpha)
                .background(Color.White.copy(alpha = 0.3f), CircleShape)
        )

        // Main circle
        Surface(
            modifier = Modifier
                .size(280.dp)
                .scale(scale),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(3.dp, Color.White.copy(alpha = 0.3f))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = emoji,
                    fontSize = 120.sp
                )
            }
        }
    }
}

@Composable
fun FloatingEmojis(emojis: List<String>, isVisible: Boolean) {
    val positions = listOf(
        Pair(-100.dp, -100.dp),  // Top left
        Pair(100.dp, -90.dp),    // Top right
        Pair(-90.dp, 100.dp),    // Bottom left
        Pair(110.dp, 110.dp)     // Bottom right
    )

    emojis.forEachIndexed { index, emoji ->
        FloatingEmoji(
            emoji = emoji,
            offsetX = positions[index].first,
            offsetY = positions[index].second,
            delay = index * 500L,
            isVisible = isVisible
        )
    }
}

@Composable
fun FloatingEmoji(
    emoji: String,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    delay: Long,
    isVisible: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float_emoji")

    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing, delayMillis = delay.toInt()),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing, delayMillis = delay.toInt()),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Text(
        text = emoji,
        fontSize = 30.sp,
        modifier = Modifier
            .offset(x = offsetX, y = offsetY + floatY.dp)
            .alpha(if (isVisible) 0.8f else 0f)
    )
}

@Composable
fun PageIndicators(
    pageCount: Int,
    currentPage: Int,
    onPageClick: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = currentPage == index
            val width by animateDpAsState(
                targetValue = if (isActive) 30.dp else 10.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "indicator_width"
            )

            Surface(
                modifier = Modifier
                    .width(width)
                    .height(10.dp),
                shape = RoundedCornerShape(5.dp),
                color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                onClick = { onPageClick(index) }
            ) {}
        }
    }
}

@Composable
fun NavigationButtons(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onGetStarted: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back button
        AnimatedVisibility(
            visible = currentPage > 0,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Button(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "Back",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Next or Get Started button
        Button(
            onClick = if (currentPage == totalPages - 1) onGetStarted else onNext,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp
            )
        ) {
            Text(
                text = if (currentPage == totalPages - 1) "Get Started" else "Next",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF667eea),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingScreenPreview() {
    MaterialTheme {
        OnboardingScreen(onFinish = {})
    }
}