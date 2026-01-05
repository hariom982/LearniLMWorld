package com.example.learnilmworld

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.learnilmworld.retrofit.RetrofitClient
import com.example.learnilmworld.retrofit.TokenResponse
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoCallActivity : AppCompatActivity() {

    private val appID: Long = 1029743296L

    companion object {
        private const val EXTRA_CALL_ID = "call_id"
        private const val EXTRA_USER_ID = "user_id"
        private const val EXTRA_USER_NAME = "user_name"

        fun startMeeting(
            context: Context,
            callID: String,
            userID: String,
            userName: String
        ) {
            val intent = Intent(context, VideoCallActivity::class.java).apply {
                putExtra(EXTRA_CALL_ID, callID)
                putExtra(EXTRA_USER_ID, userID)
                putExtra(EXTRA_USER_NAME, userName)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide action bar for full-screen call
        supportActionBar?.hide()

        val callID = intent.getStringExtra(EXTRA_CALL_ID) ?: ""
        val userID = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: ""

        if (callID.isEmpty() || userID.isEmpty() || userName.isEmpty()) {
            Toast.makeText(this, "Invalid call details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

//        val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
//
//        // Customizable tutoring experience
//        config.turnOnCameraWhenJoining = true
//        config.turnOnMicrophoneWhenJoining = true
//        config.useSpeakerWhenJoining = true
//
//        val galleryConfig = ZegoLayoutGalleryConfig()
//        // Auto-switch to fullscreen when screen sharing starts
//        galleryConfig.showNewScreenSharingViewInFullscreenMode = true
//        // Show toggle button for fullscreen when screen is pressed
//        galleryConfig.showScreenSharingFullscreenModeToggleButtonRules =
//            com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules.SHOW_WHEN_SCREEN_PRESSED
//        config.layout = ZegoLayout(ZegoLayoutMode.GALLERY, galleryConfig)
//
//        config.bottomMenuBarConfig.buttons = mutableListOf(
//            ZegoMenuBarButtonName.TOGGLE_CAMERA_BUTTON,
//            ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON,
//            ZegoMenuBarButtonName.SWITCH_CAMERA_BUTTON,
//            ZegoMenuBarButtonName.HANG_UP_BUTTON,
//            ZegoMenuBarButtonName.SCREEN_SHARING_TOGGLE_BUTTON
//        )
//
//        val fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
//            appID,
//            appSign,
//            userID,
//            userName,
//            callID,
//            config
//        )
//
//        supportFragmentManager.beginTransaction()
//            .replace(android.R.id.content, fragment)
//            .commit()
        // Fetch token using Retrofit
        fetchToken(
            userId = userID,
            onSuccess = { token ->
                addCallFragment(userID, userName, callID, token)
            },
            onFailure = { error ->
                Log.e("VideoCallActivity", "Failed to fetch token: $error")
                Toast.makeText(this, "Failed to connect: $error", Toast.LENGTH_LONG).show()
                finish()
            }
        )
    }

    private fun fetchToken(
        userId: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val requestBody = mapOf("userId" to userId)

        RetrofitClient.api.getToken(requestBody).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                if (response.isSuccessful && response.body()?.token != null) {
                    onSuccess(response.body()!!.token)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    onFailure("Server error: ${response.code()} - $errorMsg")
                }
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                onFailure("Network error: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun addCallFragment(
        userId: String,
        userName: String,
        sessionId: String,
        token: String
    ) {
        val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()

        // Customize for tutoring experience
        config.turnOnCameraWhenJoining = true
        config.turnOnMicrophoneWhenJoining = true
        config.useSpeakerWhenJoining = true

        val galleryConfig = ZegoLayoutGalleryConfig()
        // Optional: Auto-switch to fullscreen when screen sharing starts
        galleryConfig.showNewScreenSharingViewInFullscreenMode = true
        // Optional: Show toggle button for fullscreen when screen is pressed
        galleryConfig.showScreenSharingFullscreenModeToggleButtonRules =
            com.zegocloud.uikit.components.common.ZegoShowFullscreenModeToggleButtonRules.SHOW_WHEN_SCREEN_PRESSED
        config.layout = ZegoLayout(ZegoLayoutMode.GALLERY, galleryConfig)

        config.bottomMenuBarConfig.buttons = mutableListOf(
            ZegoMenuBarButtonName.TOGGLE_CAMERA_BUTTON,
            ZegoMenuBarButtonName.TOGGLE_MICROPHONE_BUTTON,
            ZegoMenuBarButtonName.SWITCH_CAMERA_BUTTON,
            ZegoMenuBarButtonName.HANG_UP_BUTTON,
            ZegoMenuBarButtonName.SCREEN_SHARING_TOGGLE_BUTTON
        )

        val fragment = ZegoUIKitPrebuiltCallFragment.newInstanceWithToken(
            appID,
            token,
            userId,
            userName,
            sessionId,
            config
        )

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack("call_fragment") // Prevents instant jump back
            .commit()
    }
}