package com.example.learnilmworld.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL
import java.util.*

/**
 * Jitsi Meeting Manager with Native SDK Integration
 * Handles video calling with in-app Jitsi Meet experience
 */
object JitsiMeetingManager {

    private const val CAMERA_PERMISSION_REQUEST = 101
    private const val AUDIO_PERMISSION_REQUEST = 102
    private const val PERMISSIONS_REQUEST_CODE = 103

    // Required permissions for Jitsi
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.MODIFY_AUDIO_SETTINGS
    )

    /**
     * Generate a unique room name for the session
     */
    fun generateRoomName(sessionId: String): String {
        return "LearnIlm-$sessionId"
    }

    /**
     * Generate meeting link (for sharing)
     */
    fun generateMeetingLink(roomName: String): String {
        return "https://meet.jit.si/$roomName"
    }

    /**
     * Check if all required permissions are granted
     */
    fun hasRequiredPermissions(context: Context): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Request required permissions
     */
    fun requestPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            REQUIRED_PERMISSIONS,
            PERMISSIONS_REQUEST_CODE
        )
    }

    /**
     * Check if permissions were granted from request result
     */
    fun onPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ): Boolean {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        }
        return false
    }

    /**
     * Create session and save meeting details to Firestore
     */
    suspend fun createMeetingSession(
        sessionRequestId: String,
        trainerName: String,
        studentName: String
    ): MeetingDetails {
        val firestore = FirebaseFirestore.getInstance()

        // Generate unique room name
        val roomName = generateRoomName(sessionRequestId)
        val meetingLink = generateMeetingLink(roomName)

        // Save to Firestore
        firestore.collection("sessionRequests")
            .document(sessionRequestId)
            .update(
                mapOf(
                    "meetingLink" to meetingLink,
                    "roomName" to roomName,
                    "meetingType" to "JITSI",
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .await()

        return MeetingDetails(
            roomName = roomName,
            meetingLink = meetingLink,
            trainerName = trainerName,
            studentName = studentName
        )
    }

    /**
     * Launch Jitsi Meet with native SDK (In-app experience)
     */
    fun launchJitsiMeet(
        context: Context,
        roomName: String,
        userName: String,
        isTrainer: Boolean = false,
        onError: ((String) -> Unit)? = null
    ) {
        try {
            // Check permissions
            if (!hasRequiredPermissions(context)) {
                if (context is Activity) {
                    requestPermissions(context)
                }
                onError?.invoke("Camera and microphone permissions are required")
                return
            }

            // Build conference options
            val options = JitsiMeetConferenceOptions.Builder()
                .setServerURL(URL("https://meet.jit.si"))
                .setRoom(roomName)
                .setSubject("Learning Session")
                .setAudioMuted(false)
                .setVideoMuted(false)
                .setAudioOnly(false)
                .setConfigOverride("requireDisplayName", true)
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
                .setUserInfo(
                    org.jitsi.meet.sdk.JitsiMeetUserInfo().apply {
                        displayName = userName
                        email = "" // Optional: Add user email
                        avatar = null // Optional: Add user avatar URL
                    }
                )
                .build()

            // Launch Jitsi Activity
            JitsiMeetActivity.launch(context, options)

        } catch (e: Exception) {
            onError?.invoke("Failed to start video call: ${e.message}")
            Toast.makeText(
                context,
                "Failed to start meeting: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Alternative: Open in browser if native fails
     */
    fun openInBrowser(context: Context, meetingLink: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(meetingLink))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to open meeting: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Copy meeting link to clipboard
     */
    fun copyMeetingLink(context: Context, meetingLink: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Meeting Link", meetingLink)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "Meeting link copied!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Share meeting link via other apps
     */
    fun shareMeetingLink(
        context: Context,
        meetingLink: String,
        sessionTitle: String = "Learning Session"
    ) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Join $sessionTitle")
            putExtra(
                Intent.EXTRA_TEXT,
                """
                Join our learning session!
                
                Meeting Link: $meetingLink
                
                Click the link to join the video call.
                """.trimIndent()
            )
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share meeting link"))
    }

    /**
     * Get meeting details from Firestore
     */
    suspend fun getMeetingDetails(sessionRequestId: String): MeetingDetails? {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val doc = firestore.collection("sessionRequests")
                .document(sessionRequestId)
                .get()
                .await()

            val roomName = doc.getString("roomName")
            val meetingLink = doc.getString("meetingLink")
            val trainerName = doc.getString("trainerName")
            val studentName = doc.getString("studentName")

            if (roomName != null && meetingLink != null) {
                MeetingDetails(
                    roomName = roomName,
                    meetingLink = meetingLink,
                    trainerName = trainerName ?: "",
                    studentName = studentName ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Check if meeting is currently active
     * (You can enhance this by tracking active participants in Firestore)
     */
    suspend fun isMeetingActive(sessionRequestId: String): Boolean {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            val doc = firestore.collection("sessionRequests")
                .document(sessionRequestId)
                .get()
                .await()

            doc.getString("status") == "confirmed"
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Data class for meeting details
 */
data class MeetingDetails(
    val roomName: String,
    val meetingLink: String,
    val trainerName: String,
    val studentName: String
)

/**
 * Extension function for Activity to handle permission results
 */
fun Activity.handleJitsiPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    if (JitsiMeetingManager.onPermissionsResult(requestCode, permissions, grantResults)) {
        onGranted()
    } else {
        onDenied()
        Toast.makeText(
            this,
            "Camera and microphone permissions are required for video calls",
            Toast.LENGTH_LONG
        ).show()
    }
}