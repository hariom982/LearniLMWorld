package com.example.learnilmworld

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

data class OnboardingPage(
    val imageRes: Int?, // Change from emoji to image resource
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

    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding1, // Replace with your image resource
            title = "Learn Languages\nFrom Anywhere",
            description = "Connect with native speakers and expert trainers from around the world to master any language.",
            floatingEmojis = listOf("🇪🇸", "🇫🇷", "🇩🇪", "🇯🇵")
        ),
        OnboardingPage(
            imageRes = R.drawable.imageremovebgpreview, // Replace with your image resource
            title = "Expert Native\nTrainers",
            description = "Learn from certified language experts who bring authentic cultural insights to every lesson.",
            floatingEmojis = listOf("💬", "📚", "🎯", "⭐")
        ),
        OnboardingPage(
            imageRes = R.drawable.image3, // Replace with your image resource
            title = "Interactive\nLearning Tools",
            description = "Engage with videos, quizzes, live sessions, and personalized practice to accelerate your progress.",
            floatingEmojis = listOf("🎥", "🎧", "📝", "🏆")
        ),
        OnboardingPage(
            imageRes = R.drawable.image4, // Replace with your image resource
            title = "Start Your\nJourney Today",
            description = "Join thousands of learners achieving fluency. Your language adventure begins now!",
            floatingEmojis = listOf("✨", "🎊", "🌟", "💫")
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.learnilmbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Animated particles background
        FloatingParticles()

        AppNameHeader()

        // Content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {

                // Pager (WITHOUT image now)
                HorizontalPager(
                    count = pages.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { }

                // Illustration placed just above card (overlapping)
                OnboardingImageContent(
                    page = pages[pagerState.currentPage],
                    isVisible = true,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 50.dp)   // move image slightly down to touch card
                )
            }

            // White card containing title, description, indicators, and buttons
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    OnboardingText(
                        page = pages[pagerState.currentPage],
                        isVisible = true
                    )

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

                    Spacer(modifier = Modifier.height(32.dp))

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
                }
            }
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
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo2),
                contentDescription = "App Logo",
                modifier = Modifier.size(250.dp)
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
            .background(color = Color.White, shape = CircleShape)
    )
}

@Composable
fun OnboardingImageContent(
    page: OnboardingPage,
    isVisible: Boolean,
    modifier: Modifier = Modifier
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

    Box(
        modifier = Modifier
            .height(200.dp)
            .scale(scale)
            .alpha(alpha),
    ) {
        // Illustration only
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            PulsingIllustrationWithImage(imageRes = page.imageRes, isVisible = isVisible)
        }
    }
}

@Composable
fun OnboardingText(
    page: OnboardingPage,
    isVisible: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = page.title,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3F51B5),
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun PulsingIllustrationWithImage(imageRes: Int?, isVisible: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Onboarding illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun FloatingEmojis(emojis: List<String>, isVisible: Boolean) {
    val positions = listOf(
        Pair(-110.dp, -110.dp),  // Top left
        Pair(110.dp, -100.dp),   // Top right
        Pair(-100.dp, 110.dp),   // Bottom left
        Pair(120.dp, 120.dp)     // Bottom right
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
                color = if (isActive) Color(0xFF3F51B5) else Color(0xFF3F51B5).copy(alpha = 0.3f),
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back button
        AnimatedVisibility(
            visible = currentPage > 0,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF3F51B5).copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF3F51B5)
                )
            ) {
                Text(
                    text = "Back",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
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
                containerColor = Color(0xFF3F51B5)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = if (currentPage == totalPages - 1) "Get Started" else "Next",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
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