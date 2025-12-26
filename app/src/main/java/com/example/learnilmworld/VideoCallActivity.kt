package com.example.learnilmworld

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayout
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutGalleryConfig
import com.zegocloud.uikit.components.audiovideocontainer.ZegoLayoutMode
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment
import com.zegocloud.uikit.prebuilt.call.config.ZegoMenuBarButtonName

class VideoCallActivity : AppCompatActivity() {

    private val appID: Long = 1752748370L
    private val appSign: String = "92a9b47c19a08706ea95fdd8451a169b57ea0a3d15471e90c92ffb5f6ae0b6f5"

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

        val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()

        // Customizable tutoring experience
        config.turnOnCameraWhenJoining = true
        config.turnOnMicrophoneWhenJoining = true
        config.useSpeakerWhenJoining = true

        val galleryConfig = ZegoLayoutGalleryConfig()
        // Auto-switch to fullscreen when screen sharing starts
        galleryConfig.showNewScreenSharingViewInFullscreenMode = true
        // Show toggle button for fullscreen when screen is pressed
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

        val fragment = ZegoUIKitPrebuiltCallFragment.newInstance(
            appID,
            appSign,
            userID,
            userName,
            callID,
            config
        )

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit()
    }
}