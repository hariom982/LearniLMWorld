package com.example.learnilmworld.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnilmworld.models.User
import com.example.learnilmworld.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Check if user is already logged in
        checkCurrentUser()
    }

    // Check if user is logged in
    private fun checkCurrentUser() {
        viewModelScope.launch {
            if (repository.isUserLoggedIn()) {
                val result = repository.getCurrentUserData()
                result.fold(
                    onSuccess = { user ->
                        user?.let {
                            _currentUser.value = it
                            _authState.value = AuthState.Success(it)
                        }
                    },
                    onFailure = {
                        _authState.value = AuthState.Idle
                    }
                )
            }
        }
    }

    // Register Student
    fun registerStudent(
        email: String,
        password: String,
        fullName: String,
        lastName: String,
        phoneNumber: String,
        nativeLanguage: String,
        languagesToLearn: List<String>,
        learningLevel: String,
        location: String = "",
        qualification: String = "",
        college: String = ""
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.registerStudent(
                email = email,
                password = password,
                fullName = fullName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                nativeLanguage = nativeLanguage,
                languagesToLearn = languagesToLearn,
                learningLevel = learningLevel,
                location = location,
                qualification = qualification,
                college = college
            )

            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    // Register Trainer
    fun registerTrainer(
        email: String,
        password: String,
        fullName: String,
        lastName: String,
        phoneNumber: String,
        bio: String,
        yearsOfExperience: Int,
        hourlyRate: Double,
        teachingStyle: String,
        languagesToTeach: List<String>,
        specializations: List<String>,
        certification: String,
        nationality: String = ""
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.registerTrainer(
                email = email,
                password = password,
                fullName = fullName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                bio = bio,
                yearsOfExperience = yearsOfExperience,
                hourlyRate = hourlyRate,
                teachingStyle = teachingStyle,
                languagesToTeach = languagesToTeach,
                specializations = specializations,
                certification = certification,
                nationality = nationality
            )

            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    // Login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.loginUser(email, password)

            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Login failed"
                    )
                }
            )
        }
    }

    // Logout
    fun logout() {
        repository.logoutUser()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    // Reset State
    fun resetState() {
        _authState.value = AuthState.Idle
    }

    // Reset Password
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.resetPassword(email)

            result.fold(
                onSuccess = {
                    _authState.value = AuthState.Idle
                    // Show success message
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Password reset failed"
                    )
                }
            )
        }
    }

    // Update User Profile
    fun updateProfile(updates: Map<String, Any>) {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                repository.updateUserData(user.uid, updates)
            }
        }
    }
}
