package com.example.learnilmworld.meeting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.learnilmworld.utils.JitsiMeetingManager
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

/**
 * Wrapper Activity for Jitsi Meet with proper permission handling
 */
class JitsiMeetingActivity : ComponentActivity() {

    private var roomName: String = ""
    private var userName: String = ""
    private var meetingLink: String = ""

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }

        if (allGranted) {
            // All permissions granted, launch Jitsi
            launchJitsiMeeting()
        } else {
            // Permissions denied
            Toast.makeText(
                this,
                "Camera and microphone permissions are required for video calls",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    companion object {
        private const val EXTRA_ROOM_NAME = "room_name"
        private const val EXTRA_USER_NAME = "user_name"
        private const val EXTRA_MEETING_LINK = "meeting_link"

        fun startMeeting(
            context: Context,
            roomName: String,
            userName: String,
            meetingLink: String
        ) {
            val intent = Intent(context, JitsiMeetingActivity::class.java).apply {
                putExtra(EXTRA_ROOM_NAME, roomName)
                putExtra(EXTRA_USER_NAME, userName)
                putExtra(EXTRA_MEETING_LINK, meetingLink)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get meeting details from intent
        roomName = intent.getStringExtra(EXTRA_ROOM_NAME) ?: ""
        userName = intent.getStringExtra(EXTRA_USER_NAME) ?: ""
        meetingLink = intent.getStringExtra(EXTRA_MEETING_LINK) ?: ""

        if (roomName.isEmpty() || userName.isEmpty()) {
            Toast.makeText(this, "Invalid meeting details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Check and request permissions
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
        )

        val permissionsToRequest = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isEmpty()) {
            // All permissions already granted
            launchJitsiMeeting()
        } else {
            // Request missing permissions
            permissionLauncher.launch(requiredPermissions)
        }
    }

    private fun launchJitsiMeeting() {
        try {
            // Build conference options
            val options = JitsiMeetConferenceOptions.Builder()
                .setServerURL(URL("https://meet.jit.si"))
                .setRoom(roomName)
                .setSubject("LearnIlm Learning Session")
                .setAudioMuted(false)
                .setVideoMuted(false)
                .setAudioOnly(false)
                .setConfigOverride("requireDisplayName", true)
                .setConfigOverride("prejoinPageEnabled", false)
                .setFeatureFlag("chat.enabled", true)
                .setFeatureFlag("pip.enabled", true)
                .setFeatureFlag("recording.enabled", false)
                .setFeatureFlag("live-streaming.enabled", false)
                .setFeatureFlag("android.screensharing.enabled", true)
                .setFeatureFlag("raise-hand.enabled", true)
                .setFeatureFlag("tile-view.enabled", true)
                .setFeatureFlag("toolbox.alwaysVisible", false)
                .setFeatureFlag("add-people.enabled", false)
                .setFeatureFlag("invite.enabled", false)
                .setFeatureFlag("meeting-name.enabled", true)
                .setFeatureFlag("calendar.enabled", false)
                .setFeatureFlag("call-integration.enabled", false)
                .setUserInfo(
                    org.jitsi.meet.sdk.JitsiMeetUserInfo().apply {
                        displayName = userName
                    }
                )
                .build()

            // Launch Jitsi Meet Activity
            JitsiMeetActivity.launch(this, options)

            // Finish this wrapper activity
            finish()

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Failed to start meeting: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
}