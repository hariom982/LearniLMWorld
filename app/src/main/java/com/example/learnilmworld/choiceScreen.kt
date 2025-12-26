package com.example.learnilmworld
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.learnilmworld.screen.Screen
import kotlinx.coroutines.delay

@Composable
fun choiceScreen(navController: NavHostController) {
    var selectedUserType by remember { mutableStateOf<UserType?>(null) }

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
        // Animated floating shapes in background
        FloatingShapes()

        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with pulse animation
            item{
                AnimatedLogo()
            }

            item{
                Spacer(modifier = Modifier.height(16.dp))
            }

            // App Title
            item{
                Text(
                    text = "LearniLMWorld",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item{
                Text(
                    text = "Master Languages Together",
                    fontSize = 17.sp,
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 48.dp)
                )
            }

            // Sign Up Prompt
            item{
                Text(
                    text = "Choose Your Journey",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier.padding(bottom = 28.dp)
                )
            }


            // Student Option
            item{
                GlassmorphicUserCard(
                    userType = UserType.STUDENT,
                    icon = Icons.Default.School,
                    title = "I'm a Student",
                    description = "Start your journey",
                    isSelected = selectedUserType == UserType.STUDENT,
                    onClick = { selectedUserType = UserType.STUDENT }
                )
            }

            item{
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Trainer Option
            item{
                GlassmorphicUserCard(
                    userType = UserType.TRAINER,
                    icon = Icons.Default.Person,
                    title = "I'm a Trainer",
                    description = "Be a teacher",
                    isSelected = selectedUserType == UserType.TRAINER,
                    onClick = { selectedUserType = UserType.TRAINER }
                )
            }

            item{
                Spacer(modifier = Modifier.height(36.dp))
            }

            // Continue Button with shimmer effect
            item{
                ContinueButton(
                    enabled = selectedUserType != null,
                    onClick = {
                        when (selectedUserType) {
                            UserType.STUDENT -> navController.navigate(Screen.StudentSignup.route)
                            UserType.TRAINER -> navController.navigate(Screen.TrainerSignup.route)
                            null -> {}
                        }
                    }
                )
            }

            item{
                Spacer(modifier = Modifier.height(24.dp))
            }

            item{
                // Sign In Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Sign In",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            navController.navigate("signin")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .background(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🌍",
            fontSize = 40.sp
        )
    }
}

@Composable
fun FloatingShapes() {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    // Shape 1
    val offset1 by infiniteTransition.animateOffset(
        initialValue = androidx.compose.ui.geometry.Offset(0f, 0f),
        targetValue = androidx.compose.ui.geometry.Offset(30f, -30f),
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shape1"
    )

    // Shape 2
    val offset2 by infiniteTransition.animateOffset(
        initialValue = androidx.compose.ui.geometry.Offset(0f, 0f),
        targetValue = androidx.compose.ui.geometry.Offset(-20f, 20f),
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shape2"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = offset1.x.dp, y = offset1.y.dp)
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-150).dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = offset2.x.dp, y = offset2.y.dp)
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 100.dp)
                .background(Color(0xFFFFD700).copy(alpha = 0.1f), CircleShape)
        )
    }
}

@Composable
fun GlassmorphicUserCard(
    userType: UserType,
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 25.dp else 10.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elevation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val backgroundColor = if (isSelected) {
        Color.White.copy(alpha = 0.95f)
    } else {
        Color.White.copy(alpha = 0.15f)
    }

    val borderColor = if (isSelected) {
        Color.White.copy(alpha = 0.9f)
    } else {
        Color.White.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(animatedScale)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = animatedElevation
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Icon Container with rotation animation
                AnimatedIconContainer(
                    icon = icon,
                    isSelected = isSelected
                )

                // Text Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1F2937) else Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = if (isSelected) Color(0xFF6B7280) else Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }

                // Checkmark with rotation animation
                AnimatedCheckmark(isSelected = isSelected)
            }
        }
    }
}

@Composable
fun AnimatedIconContainer(
    icon: ImageVector,
    isSelected: Boolean
) {
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 5f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_scale"
    )

    Box(
        modifier = Modifier
            .size(72.dp)
            .rotate(rotation)
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 12.dp else 8.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                brush = if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    )
                },
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            tint = Color.White
        )
    }
}

@Composable
fun AnimatedCheckmark(isSelected: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmark_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 0f else -180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "checkmark_rotation"
    )

    if (scale > 0f) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .scale(scale)
                .rotate(rotation)
                .shadow(8.dp, CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF10B981),
                            Color(0xFF059669)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ContinueButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF667eea),
            disabledContainerColor = Color.White.copy(alpha = 0.2f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = "Continue",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

enum class UserType {
    STUDENT,
    TRAINER
}


@Composable
fun InfiniteTransition.animateOffset(
    initialValue: androidx.compose.ui.geometry.Offset,
    targetValue: androidx.compose.ui.geometry.Offset,
    animationSpec: InfiniteRepeatableSpec<androidx.compose.ui.geometry.Offset>,
    label: String
): State<androidx.compose.ui.geometry.Offset> {
    return animateValue(
        initialValue = initialValue,
        targetValue = targetValue,
        typeConverter = androidx.compose.ui.geometry.Offset.VectorConverter,
        animationSpec = animationSpec,
        label = label
    )
}