package com.example.learnilmworld.student

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class OnboardingData(
    var languagesToLearn: MutableSet<String> = mutableSetOf(),
    var currentLevel: String = "",
    var mainGoals: MutableSet<String> = mutableSetOf(),
    var preferredFormat: String = "",
    var allowNotifications: Boolean = false
)

// Enum representing each logical screen/question
enum class OnboardingStep {
    LANGUAGES,  // Step index 0
    LEVEL,      // Step index 1 — skipped when multiple languages selected
    GOALS,      // Step index 2
    FORMAT,     // Step index 3
    NOTIFICATIONS // Step index 4
}

@Composable
fun StudentOnboardingScreen(
    navController: NavHostController,
    studentEmail: String // Pass this from signup
) {
    // currentStep tracks position in the *visible* step list (not raw indices)
    var currentStep by remember { mutableStateOf(0) }
    val onboardingData = remember { mutableStateOf(OnboardingData()) }
    val context = LocalContext.current

    var customLanguage by remember { mutableStateOf("") }
    var customGoal by remember { mutableStateOf("") }
    var showLanguageInput by remember { mutableStateOf(false) }
    var showGoalInput by remember { mutableStateOf(false) }

    val languages = listOf("English", "Spanish", "French", "Hindi", "German", "Japanese", "Chinese", "Other")
    val levels = listOf("Beginner", "Elementary", "Intermediate", "Upper-Intermediate", "Advanced")
    val goals = listOf("Conversational fluency", "Travel", "Work", "Exam prep (IELTS/TOEFL)", "Academic", "Kids learning", "Hobby", "Other")
    val formats = listOf("Self-paced lessons", "Live group classes", "1:1 tutor", "Mix")

    // Determine whether to show the Level step based on language selection count
    val isMultiLanguage = onboardingData.value.languagesToLearn.size > 1

    // Build the ordered list of steps to show based on language selection
    val visibleSteps: List<OnboardingStep> = remember(isMultiLanguage) {
        if (isMultiLanguage) {
            listOf(
                OnboardingStep.LANGUAGES,
                OnboardingStep.GOALS,
                OnboardingStep.FORMAT,
                OnboardingStep.NOTIFICATIONS
            )
        } else {
            listOf(
                OnboardingStep.LANGUAGES,
                OnboardingStep.LEVEL,
                OnboardingStep.GOALS,
                OnboardingStep.FORMAT,
                OnboardingStep.NOTIFICATIONS
            )
        }
    }

    // Clamp currentStep if visibleSteps shrinks (e.g. user goes back to step 0 and adds more languages)
    val safeCurrentStep = currentStep.coerceAtMost(visibleSteps.lastIndex)
    if (safeCurrentStep != currentStep) currentStep = safeCurrentStep

    val totalSteps = visibleSteps.size
    val progress = (currentStep + 1) / totalSteps.toFloat()

    // Which logical screen is active right now
    val activeStep = visibleSteps[currentStep]

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    IconButton(
                        onClick = { currentStep-- },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(44.dp))
                }

                Text(
                    text = "Step ${currentStep + 1} of $totalSteps",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .shadow(4.dp, RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Question Content — driven by activeStep enum
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // LANGUAGES
                this@Column.AnimatedVisibility(
                    visible = activeStep == OnboardingStep.LANGUAGES,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    MultiSelectQuestionStep(
                        emoji = "🌍",
                        question = "Which languages do you want to learn?",
                        subtitle = "Select one or more",
                        options = languages,
                        selectedOptions = onboardingData.value.languagesToLearn,
                        onOptionToggled = { option ->
                            if (option == "Other") {
                                showLanguageInput = !showLanguageInput
                                if (!showLanguageInput) {
                                    customLanguage = ""
                                    onboardingData.value.languagesToLearn.remove(customLanguage)
                                }
                            } else {
                                val newSet = onboardingData.value.languagesToLearn.toMutableSet()
                                if (newSet.contains(option)) newSet.remove(option) else newSet.add(option)
                                onboardingData.value = onboardingData.value.copy(languagesToLearn = newSet)
                            }
                        },
                        showCustomInput = showLanguageInput,
                        customInputValue = customLanguage,
                        onCustomInputChange = {
                            customLanguage = it
                            val newSet = onboardingData.value.languagesToLearn.toMutableSet()
                            newSet.removeAll { lang -> !languages.contains(lang) }
                            if (it.isNotEmpty()) newSet.add(it)
                            onboardingData.value = onboardingData.value.copy(languagesToLearn = newSet)
                        },
                        customInputLabel = "Enter language name"
                    )
                }

                // LEVEL — only shown when single language selected
                this@Column.AnimatedVisibility(
                    visible = activeStep == OnboardingStep.LEVEL,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    QuestionStep(
                        emoji = "📊",
                        question = "What is your current level?",
                        options = levels,
                        selectedOption = onboardingData.value.currentLevel,
                        onOptionSelected = { onboardingData.value = onboardingData.value.copy(currentLevel = it) }
                    )
                }

                // GOALS
                this@Column.AnimatedVisibility(
                    visible = activeStep == OnboardingStep.GOALS,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    MultiSelectQuestionStep(
                        emoji = "🎯",
                        question = "What are your main goals?",
                        subtitle = "Select one or more",
                        options = goals,
                        selectedOptions = onboardingData.value.mainGoals,
                        onOptionToggled = { option ->
                            if (option == "Other") {
                                showGoalInput = !showGoalInput
                                if (!showGoalInput) {
                                    customGoal = ""
                                    onboardingData.value.mainGoals.remove(customGoal)
                                }
                            } else {
                                val newSet = onboardingData.value.mainGoals.toMutableSet()
                                if (newSet.contains(option)) newSet.remove(option) else newSet.add(option)
                                onboardingData.value = onboardingData.value.copy(mainGoals = newSet)
                            }
                        },
                        showCustomInput = showGoalInput,
                        customInputValue = customGoal,
                        onCustomInputChange = {
                            customGoal = it
                            val newSet = onboardingData.value.mainGoals.toMutableSet()
                            newSet.removeAll { goal -> !goals.contains(goal) }
                            if (it.isNotEmpty()) newSet.add(it)
                            onboardingData.value = onboardingData.value.copy(mainGoals = newSet)
                        },
                        customInputLabel = "Enter your learning goal"
                    )
                }

                // FORMAT
                this@Column.AnimatedVisibility(
                    visible = activeStep == OnboardingStep.FORMAT,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    QuestionStep(
                        emoji = "📚",
                        question = "Preferred format?",
                        options = formats,
                        selectedOption = onboardingData.value.preferredFormat,
                        onOptionSelected = { onboardingData.value = onboardingData.value.copy(preferredFormat = it) }
                    )
                }

                // NOTIFICATIONS
                this@Column.AnimatedVisibility(
                    visible = activeStep == OnboardingStep.NOTIFICATIONS,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                ) {
                    NotificationStep(
                        onboardingData = onboardingData.value,
                        onToggle = { onboardingData.value = onboardingData.value.copy(allowNotifications = it) }
                    )
                }
            }

            // Navigation Button — validation is keyed to activeStep, not raw index
            Button(
                onClick = {
                    when (activeStep) {
                        OnboardingStep.LANGUAGES -> {
                            if (onboardingData.value.languagesToLearn.isNotEmpty()) {
                                // If user had a level set but now switches to multi-language, clear it
                                if (isMultiLanguage) {
                                    onboardingData.value = onboardingData.value.copy(currentLevel = "")
                                }
                                currentStep++
                            } else {
                                Toast.makeText(context, "Please select at least one language", Toast.LENGTH_SHORT).show()
                            }
                        }
                        OnboardingStep.LEVEL -> {
                            if (onboardingData.value.currentLevel.isNotEmpty()) currentStep++
                            else Toast.makeText(context, "Please select your level", Toast.LENGTH_SHORT).show()
                        }
                        OnboardingStep.GOALS -> {
                            if (onboardingData.value.mainGoals.isNotEmpty()) currentStep++
                            else Toast.makeText(context, "Please select at least one goal", Toast.LENGTH_SHORT).show()
                        }
                        OnboardingStep.FORMAT -> {
                            if (onboardingData.value.preferredFormat.isNotEmpty()) currentStep++
                            else Toast.makeText(context, "Please select a format", Toast.LENGTH_SHORT).show()
                        }
                        OnboardingStep.NOTIFICATIONS -> {
                            Toast.makeText(context, "Profile setup complete!", Toast.LENGTH_SHORT).show()
                            navController.navigate("student_home") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(10.dp, RoundedCornerShape(18.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = if (activeStep == OnboardingStep.NOTIFICATIONS) "Complete Setup" else "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF667eea),
                    letterSpacing = 0.5.sp
                )
                if (activeStep != OnboardingStep.NOTIFICATIONS) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF667eea)
                    )
                }
            }
        }
    }
}

@Composable
fun MultiSelectQuestionStep(
    emoji: String,
    question: String,
    subtitle: String = "",
    options: List<String>,
    selectedOptions: Set<String>,
    onOptionToggled: (String) -> Unit,
    showCustomInput: Boolean = false,
    customInputValue: String = "",
    onCustomInputChange: (String) -> Unit = {},
    customInputLabel: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = emoji,
            fontSize = 50.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = question,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            val isSelected = if (option == "Other") {
                showCustomInput
            } else {
                selectedOptions.contains(option)
            }

            OptionCard(
                text = option,
                isSelected = isSelected,
                onClick = { onOptionToggled(option) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Custom input field
        AnimatedVisibility(visible = showCustomInput) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customInputValue,
                    onValueChange = onCustomInputChange,
                    label = { Text(customInputLabel) },
                    placeholder = { Text("Type here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                        focusedBorderColor = Color(0xFF667eea),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(0xFF667eea),
                        unfocusedLabelColor = Color.Black.copy(alpha = 0.7f),
                        focusedPlaceholderColor = Color.Black.copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = Color.Black.copy(alpha = 0.5f),
                        cursorColor = Color(0xFF667eea)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun QuestionStep(
    emoji: String,
    question: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = emoji,
            fontSize = 64.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = question,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        options.forEach { option ->
            OptionCard(
                text = option,
                isSelected = selectedOption == option,
                onClick = { onOptionSelected(option) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun OptionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 12.dp else 6.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) Color(0xFF667eea) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF667eea) else Color.Black.copy(alpha = 0.8f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF667eea),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationStep(
    onboardingData: OnboardingData,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Stay on track with reminders!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

//        Text(
//            text = "Get notifications for lesson reminders, special offers, and learning tips",
//            fontSize = 16.sp,
//            color = Color.White.copy(alpha = 0.9f),
//            textAlign = TextAlign.Center,
//            lineHeight = 24.sp
//        )
//
//        Spacer(modifier = Modifier.height(40.dp))

//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .shadow(8.dp, RoundedCornerShape(16.dp))
//                .background(
//                    color = Color.White.copy(alpha = 0.95f),
//                    shape = RoundedCornerShape(16.dp)
//                )
//                .padding(24.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    Text(
//                        text = "Allow notifications",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black
//                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = "You can change this anytime in settings",
//                        fontSize = 14.sp,
//                        color = Color.Black.copy(alpha = 0.6f)
//                    )
//                }
//
//                Switch(
//                    checked = onboardingData.allowNotifications,
//                    onCheckedChange = onToggle,
//                    colors = SwitchDefaults.colors(
//                        checkedThumbColor = Color.White,
//                        checkedTrackColor = Color(0xFF667eea),
//                        uncheckedThumbColor = Color.White,
//                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
//                    )
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))

        // Summary of selections
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "📋 Your Learning Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                MultiValueSummaryItem("Languages", onboardingData.languagesToLearn.toList())
                // Only show Level in summary if it was filled (single-language flow)
                if (onboardingData.currentLevel.isNotEmpty()) {
                    SummaryItem("Level", onboardingData.currentLevel)
                }
                MultiValueSummaryItem("Goals", onboardingData.mainGoals.toList())
                SummaryItem("Format", onboardingData.preferredFormat)
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.8f)
        )
        Text(
            text = value.ifEmpty { "-" },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun MultiValueSummaryItem(label: String, values: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.8f)
        )
        Text(
            text = if (values.isEmpty()) "-" else values.joinToString(", "),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 8.dp)
        )
    }
}