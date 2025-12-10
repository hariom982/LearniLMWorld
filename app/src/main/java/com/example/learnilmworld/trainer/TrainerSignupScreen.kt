package com.example.learnilmworld.trainer

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.learnilmworld.viewModel.AuthState
import com.example.learnilmworld.viewModel.AuthViewModel

@Composable
fun TrainerSignupScreen(navController: NavHostController,
                        viewModel: AuthViewModel) {
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedLanguages by remember { mutableStateOf(setOf<String>()) }
    var subjectsCanTeach by remember { mutableStateOf("") }
    var selectedStandard by remember { mutableStateOf("") }
    var customStandard by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var certification by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var resumeUri by remember { mutableStateOf<Uri?>(null) }
    var resumeFileName by remember { mutableStateOf<String?>(null) }
    var hourlyRate by remember { mutableStateOf("") }
    var teachingStyle by remember { mutableStateOf("Conversational") }
    var isLoading by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Error states
    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var languagesError by remember { mutableStateOf(false) }
    var experienceError by remember { mutableStateOf(false) }
    var hourlyRateError by remember { mutableStateOf(false) }

    val languages = listOf("Spanish", "French", "German", "Italian", "Japanese", "Korean", "Mandarin", "Portuguese")
    val standards = listOf("5-8", "5-10", "5-12", "Others")

    // Resume picker launcher
    val resumePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            resumeUri = it
            resumeFileName = it.lastPathSegment ?: "resume.pdf"
        }
    }

    // Handle authentication state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                isLoading = false
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                val route = if (state.user.userType == "TRAINER") {
                    "trainer_home"
                } else {
                    "student_home"
                }
                navController.navigate(route) {
                    popUpTo("trainer_signup") { inclusive = true }
                }
                viewModel.resetState()
            }
            is AuthState.Error -> {
                isLoading = false
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            is AuthState.Loading -> {
                isLoading = true
            }
            else -> {
                isLoading = false
            }
        }
    }

    fun validateFields(): Boolean {
        firstNameError = firstName.isBlank()
        lastNameError = lastName.isBlank()
        emailError = email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        passwordError = password.isBlank() || password.length < 6
        confirmPasswordError = confirmPassword.isBlank() || password != confirmPassword
        phoneError = phone.isBlank()
        languagesError = selectedLanguages.isEmpty()
        experienceError = experience.isBlank()
        hourlyRateError = hourlyRate.isBlank() || hourlyRate.toDoubleOrNull() == null

        return !firstNameError && !lastNameError && !emailError &&
                !passwordError && !confirmPasswordError && !phoneError &&
                !languagesError && !experienceError && !hourlyRateError
    }

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
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
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

            Spacer(modifier = Modifier.height(20.dp))

            SignupHeader(
                emoji = "👨‍🏫",
                title = "Trainer Sign Up",
                subtitle = "Share your language expertise"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FormTextField(
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        firstNameError = false
                    },
                    label = "First Name",
                    placeholder = "First name",
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        lastNameError = false
                    },
                    label = "Last Name",
                    placeholder = "Last name",
                    modifier = Modifier.weight(1f)
                )
            }
            if (firstNameError || lastNameError) {
                Text(
                    text = "First and last name are required",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = false
                },
                label = "Email Address",
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email
            )
            if (emailError) {
                Text(
                    text = "Please enter a valid email address",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password field with eye icon
            Column {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                    },
                    placeholder = { Text("Enter password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF667eea)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.95f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color(0xFF667eea),
                        unfocusedTextColor = Color(0xFF667eea),
                        focusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                if (passwordError) {
                    Text(
                        text = "Password must be at least 6 characters",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm Password field with eye icon
            Column {
                Text(
                    text = "Confirm Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = false
                    },
                    placeholder = { Text("Confirm password") },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF667eea)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.95f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color(0xFF667eea),
                        unfocusedTextColor = Color(0xFF667eea),
                        focusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = Color(0xFF667eea).copy(alpha = 0.5f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                if (confirmPasswordError) {
                    Text(
                        text = if (confirmPassword.isBlank()) "Please confirm your password" else "Passwords do not match",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = false
                },
                label = "Phone Number",
                placeholder = "Enter your mobile number",
                keyboardType = KeyboardType.Phone
            )
            if (phoneError) {
                Text(
                    text = "Phone number is required",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LanguageChipSelector(
                label = "Languages You Teach In",
                languages = languages,
                selectedLanguages = selectedLanguages,
                onLanguageToggle = { language ->
                    selectedLanguages = if (selectedLanguages.contains(language)) {
                        selectedLanguages - language
                    } else {
                        selectedLanguages + language
                    }
                    languagesError = false
                }
            )
            if (languagesError) {
                Text(
                    text = "Please select at least one language",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = subjectsCanTeach,
                onValueChange = { subjectsCanTeach = it },
                label = "Subjects you can teach",
                placeholder = "e.g. English, Hindi etc.",
                keyboardType = KeyboardType.Text
            )

            Spacer(modifier = Modifier.height(20.dp))

            StandardsSelector(
                label = "Standards You Can Teach",
                standards = standards,
                selectedStandard = selectedStandard,
                onStandardSelected = { selectedStandard = it },
                customStandard = customStandard,
                onCustomStandardChange = { customStandard = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ExperienceSelector(
                label = "Teaching Experience",
                options = listOf("1-2 years", "3-5 years", "5+ years"),
                selectedOption = experience,
                onOptionSelected = {
                    experience = it
                    experienceError = false
                }
            )
            if (experienceError) {
                Text(
                    text = "Please select your experience level",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = certification,
                onValueChange = { certification = it },
                label = "Certification/Qualification (Optional)",
                placeholder = "e.g., TEFL, CELTA, Native Speaker"
            )

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = bio,
                onValueChange = { bio = it },
                label = "Brief Bio (Optional)",
                placeholder = "Tell us about your teaching experience and methodology...",
                singleLine = false,
                minLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            ResumeUploadSection(
                resumeFileName = resumeFileName,
                onUploadClick = { resumePicker.launch("application/pdf") },
                onRemoveClick = {
                    resumeUri = null
                    resumeFileName = null
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            FormTextField(
                value = hourlyRate,
                onValueChange = {
                    hourlyRate = it
                    hourlyRateError = false
                },
                label = "Hourly Rate (USD)",
                placeholder = "e.g. 25",
                keyboardType = KeyboardType.Number
            )
            if (hourlyRateError) {
                Text(
                    text = "Please enter a valid hourly rate",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (validateFields()) {
                        val yearsOfExperience = when (experience) {
                            "1-2 years" -> 1
                            "3-5 years" -> 3
                            "5+ years" -> 5
                            else -> 0
                        }

                        val finalStandard = if (selectedStandard == "Others") {
                            customStandard
                        } else {
                            selectedStandard
                        }

                        viewModel.registerTrainer(
                            email = email,
                            password = password,
                            fullName = "$firstName ",
                            lastName = "$lastName",
                            phoneNumber = phone,
                            bio = bio,
                            yearsOfExperience = yearsOfExperience,
                            hourlyRate = hourlyRate.toDoubleOrNull() ?: 0.0,
                            teachingStyle = teachingStyle,
                            languagesToTeach = selectedLanguages.toList(),
                            specializations = subjectsCanTeach.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            certification = certification,
                            nationality = ""
                        )
                    } else {
                        Toast.makeText(context, "Please fill all required fields correctly", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(10.dp, RoundedCornerShape(18.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF667eea)
                    )
                } else {
                    Text(
                        text = "Create Trainer Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF667eea),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Keep all other composable functions unchanged
@Composable
fun StandardsSelector(
    label: String,
    standards: List<String>,
    selectedStandard: String,
    onStandardSelected: (String) -> Unit,
    customStandard: String,
    onCustomStandardChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                standards.forEach { standard ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStandard == standard,
                            onClick = { onStandardSelected(standard) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF667eea),
                                unselectedColor = Color(0xFF6B7280)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = standard,
                            fontSize = 15.sp,
                            color = Color(0xFF1F2937),
                            fontWeight = if (selectedStandard == standard) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }

                if (selectedStandard == "Others") {
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = customStandard,
                        onValueChange = onCustomStandardChange,
                        placeholder = { Text("Enter custom standard (e.g., 8-12, K-5)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3F4F6),
                            unfocusedContainerColor = Color(0xFFF3F4F6),
                            focusedIndicatorColor = Color(0xFF667eea),
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
fun ResumeUploadSection(
    resumeFileName: String?,
    onUploadClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column {
        Text(
            text = "Upload Resume (Optional)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        if (resumeFileName == null) {
            Surface(
                onClick = onUploadClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Upload Resume",
                        tint = Color(0xFF667eea),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Choose File (PDF, DOC)",
                        fontSize = 15.sp,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF10B981).copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Resume",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = resumeFileName,
                                fontSize = 14.sp,
                                color = Color(0xFF1F2937),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Resume uploaded successfully",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Accepted formats: PDF, DOC, DOCX (Max 5MB)",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

// Reusable Components (keeping all existing ones)
@Composable
fun SignupHeader(emoji: String, title: String, subtitle: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White.copy(alpha = 0.95f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            minLines = minLines
        )
    }
}

@Composable
fun LanguageChipSelector(
    label: String,
    languages: List<String>,
    selectedLanguages: Set<String>,
    onLanguageToggle: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (selectedLanguages.isEmpty()) {
                    Text(
                        text = "Select languages...",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                } else {
                    Text(
                        text = selectedLanguages.joinToString(", "),
                        color = Color(0xFF1F2937),
                        fontSize = 15.sp
                    )
                }
            }
        }

        if (showDialog) {
            LanguageSelectionDialog(
                availableLanguages = languages,
                selectedLanguages = selectedLanguages,
                onLanguageToggle = onLanguageToggle,
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    availableLanguages: List<String>,
    selectedLanguages: Set<String>,
    onLanguageToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var customLanguage by remember { mutableStateOf("") }
    var tempSelectedLanguages by remember { mutableStateOf(selectedLanguages) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Select Languages",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Choose from list:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableLanguages.forEach { language ->
                        DialogLanguageChip(
                            language = language,
                            isSelected = tempSelectedLanguages.contains(language),
                            onClick = {
                                tempSelectedLanguages = if (tempSelectedLanguages.contains(language)) {
                                    tempSelectedLanguages - language
                                } else {
                                    tempSelectedLanguages + language
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Or add custom language:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = customLanguage,
                        onValueChange = { customLanguage = it },
                        placeholder = { Text("Enter language name") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF3F4F6),
                            unfocusedContainerColor = Color(0xFFF3F4F6),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (customLanguage.isNotBlank()) {
                                tempSelectedLanguages = tempSelectedLanguages + customLanguage.trim()
                                customLanguage = ""
                            }
                        },
                        enabled = customLanguage.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Add")
                    }
                }

                if (tempSelectedLanguages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Selected (${tempSelectedLanguages.size}):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tempSelectedLanguages.forEach { language ->
                            SelectedLanguageChip(
                                language = language,
                                onRemove = {
                                    tempSelectedLanguages = tempSelectedLanguages - language
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val languagesToAdd = tempSelectedLanguages - selectedLanguages
                    val languagesToRemove = selectedLanguages - tempSelectedLanguages

                    languagesToAdd.forEach { onLanguageToggle(it) }
                    languagesToRemove.forEach { onLanguageToggle(it) }

                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF667eea)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF6B7280))
            }
        }
    )
}

@Composable
fun DialogLanguageChip(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF667eea) else Color(0xFFF3F4F6)
    val textColor = if (isSelected) Color.White else Color(0xFF1F2937)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        modifier = Modifier.shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(20.dp))
    ) {
        Text(
            text = language,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
fun SelectedLanguageChip(
    language: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF10B981),
        modifier = Modifier.shadow(4.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = language,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Surface(
                onClick = onRemove,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "×",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        var currentSequence = mutableListOf<Placeable>()
        var currentWidth = 0
        var currentHeight = 0
        var maxHeight = 0

        measurables.forEach { measurable ->
            val placeable = measurable.measure(constraints)

            if (currentWidth + placeable.width > constraints.maxWidth && currentSequence.isNotEmpty()) {
                sequences.add(currentSequence)
                maxHeight += currentHeight + 8
                currentSequence = mutableListOf()
                currentWidth = 0
                currentHeight = 0
            }

            currentSequence.add(placeable)
            currentWidth += placeable.width + 8
            currentHeight = maxOf(currentHeight, placeable.height)
        }

        if (currentSequence.isNotEmpty()) {
            sequences.add(currentSequence)
            maxHeight += currentHeight
        }

        layout(constraints.maxWidth, maxHeight) {
            var yPosition = 0
            sequences.forEach { sequence ->
                var xPosition = 0
                var rowHeight = 0

                sequence.forEach { placeable ->
                    placeable.placeRelative(x = xPosition, y = yPosition)
                    xPosition += placeable.width + 8
                    rowHeight = maxOf(rowHeight, placeable.height)
                }

                yPosition += rowHeight + 8
            }
        }
    }
}

@Composable
fun ExperienceSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEach { option ->
                ExperienceButton(
                    text = option,
                    isSelected = selectedOption == option,
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ExperienceButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.25f)
    val textColor = if (isSelected) Color(0xFF667eea) else Color.White
    val borderColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .shadow(if (isSelected) 8.dp else 0.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun SubmitButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
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
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF667eea),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SocialLoginDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.3f),
            thickness = 1.dp
        )
        Text(
            text = "OR",
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        Divider(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.3f),
            thickness = 1.dp
        )
    }
}
//
//@Composable
//fun SocialLoginButtons(
//    onGoogleClick: () -> Unit,
//    onSecondaryClick: () -> Unit,
//    secondaryText: String,
//    secondaryEmoji: String
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        SocialButton(
//            text = "Google",
//            emoji = "🔍",
//            onClick = onGoogleClick,
//            modifier = Modifier.weight(1f)
//        )
//        SocialButton(
//            text = secondaryText,
//            emoji = secondaryEmoji,
//            onClick = onSecondaryClick,
//            modifier = Modifier.weight(1f)
//        )
//    }
//}
//
//@Composable
//fun SocialButton(
//    text: String,
//    emoji: String,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    OutlinedButton(
//        onClick = onClick,
//        modifier = modifier
//            .height(56.dp)
//            .shadow(4.dp, RoundedCornerShape(14.dp)),
//        colors = ButtonDefaults.outlinedButtonColors(
//            containerColor = Color.White.copy(alpha = 0.95f)
//        ),
//        shape = RoundedCornerShape(14.dp),
//        border = androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent)
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = emoji, fontSize = 18.sp)
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(
//                text = text,
//                fontWeight = FontWeight.SemiBold,
//                color = Color(0xFF1F2937),
//                fontSize = 15.sp
//            )
//        }
//    }
//}