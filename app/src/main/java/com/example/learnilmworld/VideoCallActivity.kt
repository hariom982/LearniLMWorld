package com.example.learnilmworld

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import com.example.learnilmworld.databinding.ActivityVideoCallBinding
import com.example.learnilmworld.retrofit.RetrofitClient
import com.example.learnilmworld.retrofit.TokenResponse
import live.hms.video.error.HMSException
import live.hms.video.media.tracks.*
import live.hms.video.sdk.*
import live.hms.video.sdk.models.*
import live.hms.video.sdk.models.enums.HMSPeerUpdate
import live.hms.video.sdk.models.enums.HMSRoomUpdate
import live.hms.video.sdk.models.enums.HMSTrackUpdate
import live.hms.video.sdk.models.trackchangerequest.HMSChangeTrackStateRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class VideoCallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoCallBinding
    private var hmsSDK: HMSSDK? = null

    // Track mute states
    private var isMicMuted = false
    private var isCamMuted = false
    private var isScreenSharing = false

    private var isSwapped = false
    private var localTrack: HMSVideoTrack? = null
    private var remoteTrack: HMSVideoTrack? = null

    private var isLocalFullScreen = false

    // Session timer
    private val timerHandler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0L
    private val timerRunnable = object : Runnable {
        override fun run() {
            elapsedSeconds++
            val minutes = TimeUnit.SECONDS.toMinutes(elapsedSeconds)
            val seconds = elapsedSeconds % 60
            binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            timerHandler.postDelayed(this, 1000)
        }
    }

    // Screen share result launcher
    private val screenShareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {

            hmsSDK?.startScreenshare(
                object : HMSActionResultListener {

                    override fun onSuccess() {
                        runOnUiThread {
                            isScreenSharing = true
                            updateScreenShareUI(true)
                            Toast.makeText(
                                this@VideoCallActivity,
                                "Screen sharing started",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(error: HMSException) {
                        runOnUiThread {
                            Toast.makeText(
                                this@VideoCallActivity,
                                "Screen share failed: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                },
                result.data,
                null
            )
        }
    }

    companion object {
        private const val EXTRA_ROOM_ID   = "room_id"
        private const val EXTRA_USER_ID   = "user_id"
        private const val EXTRA_USER_NAME = "user_name"
        private const val EXTRA_ROLE      = "role"

        fun startMeeting(
            context: Context,
            callID: String,
            userID: String,
            userName: String,
            role: String = "student"
        ) {
            context.startActivity(Intent(context, VideoCallActivity::class.java).apply {
                putExtra(EXTRA_ROOM_ID, callID)
                putExtra(EXTRA_USER_ID, userID)
                putExtra(EXTRA_USER_NAME, userName)
                putExtra(EXTRA_ROLE, role)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val roomId   = intent.getStringExtra(EXTRA_ROOM_ID)   ?: ""
        val userId   = intent.getStringExtra(EXTRA_USER_ID)   ?: ""
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: ""
        val role     = intent.getStringExtra(EXTRA_ROLE)      ?: "student"

        if (roomId.isEmpty() || userId.isEmpty()) {
            Toast.makeText(this, "Invalid call details", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Show participant name in top bar
        binding.tvSessionTitle.text = if (role == "trainer") "Teaching Session" else "Learning Session"

        fetchJoinToken(
            roomId, userId, role,
            onSuccess = { token -> joinRoom(token, userName) },
            onFailure = { err ->
                Toast.makeText(this, "Token error: $err", Toast.LENGTH_LONG).show()
                finish()
            }
        )

        setupControls()
    }

    private fun setupControls() {
        // End call
        binding.btnHangUp.setOnClickListener { leaveRoom() }

        // Mic toggle
        binding.btnToggleMic.setOnClickListener {
            hmsSDK?.getLocalPeer()?.audioTrack?.let { track ->
                isMicMuted = !isMicMuted
                track.setMute(isMicMuted)
                updateMicUI()
            } ?: Toast.makeText(this, "No audio track", Toast.LENGTH_SHORT).show()
        }

        // Camera toggle
        binding.btnToggleCamera.setOnClickListener {
            hmsSDK?.getLocalPeer()?.videoTrack?.let { track ->
                isCamMuted = !isCamMuted
                track.setMute(isCamMuted)
                updateCameraUI()
            } ?: Toast.makeText(this, "No video track", Toast.LENGTH_SHORT).show()
        }

        // Screen share toggle
        binding.btnScreenShare.setOnClickListener {
            if (isScreenSharing) {
                stopScreenShare()
            } else {
                startScreenShare()
            }
        }

        // Stop share from banner
        binding.btnStopShare.setOnClickListener {
            stopScreenShare()
        }

        binding.localVideoCard.setOnClickListener {
            swapVideoViews()
        }

        // Also tap remote view when it's in PiP mode to swap back
        binding.remoteVideoView.setOnClickListener {
            if (isSwapped) swapVideoViews()
        }

        // Flip camera
//        binding.btnFlipCamera.setOnClickListener {
//            hmsSDK?.getLocalPeer()?.videoTrack?.let {
//                hmsSDK?.switchCamera(object : HMSActionResultListener {
//                    override fun onSuccess() {
//                        // Camera flipped
//                    }
//                    override fun onError(error: HMSException) {
//                        runOnUiThread {
//                            Toast.makeText(this@VideoCallActivity, "Could not flip camera", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                })
//            }
//        }
      }
    private fun swapVideoViews() {
        isSwapped = !isSwapped

        val root = binding.root as androidx.constraintlayout.widget.ConstraintLayout

        if (isSwapped) {
            // ── Expand localVideoCard to fullscreen ──────────────────
            val cardParams = binding.localVideoCard.layoutParams
                    as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            cardParams.width  = 0
            cardParams.height = 0
            cardParams.topToTop     = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            cardParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            cardParams.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            cardParams.endToEnd     = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            cardParams.topMargin    = 0
            cardParams.marginEnd    = 0
            binding.localVideoCard.layoutParams = cardParams
            binding.localVideoCard.radius = 0f   // no rounding when fullscreen

            // ── Shrink remoteVideoView to PiP corner ─────────────────
            val remoteParams = binding.remoteVideoView.layoutParams
                    as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            val pipW = dpToPx(110) ; val pipH = dpToPx(160)
            remoteParams.width  = pipW
            remoteParams.height = pipH
            remoteParams.topToTop       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            remoteParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            remoteParams.startToStart   = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            remoteParams.endToEnd       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            remoteParams.topMargin  = dpToPx(108)
            remoteParams.marginEnd  = dpToPx(16)
            binding.remoteVideoView.layoutParams = remoteParams

            // Bring remote PiP on top of the fullscreen local view
            binding.remoteVideoView.elevation = dpToPx(8).toFloat()
            binding.localVideoCard.elevation  = 0f

            binding.tvPipLabel.text = binding.tvRemoteName.text.toString().ifEmpty { "Remote" }

        } else {
            // ── Restore remoteVideoView to fullscreen ────────────────
            val remoteParams = binding.remoteVideoView.layoutParams
                    as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            remoteParams.width  = 0
            remoteParams.height = 0
            remoteParams.topToTop       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            remoteParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            remoteParams.startToStart   = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            remoteParams.endToEnd       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            remoteParams.topMargin  = 0
            remoteParams.marginEnd  = 0
            binding.remoteVideoView.layoutParams = remoteParams
            binding.remoteVideoView.elevation = 0f

            // ── Restore localVideoCard to PiP corner ─────────────────
            val cardParams = binding.localVideoCard.layoutParams
                    as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            cardParams.width  = dpToPx(110)
            cardParams.height = dpToPx(160)
            cardParams.topToTop       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            cardParams.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            cardParams.startToStart   = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            cardParams.endToEnd       = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            cardParams.topMargin  = dpToPx(108)
            cardParams.marginEnd  = dpToPx(16)
            binding.localVideoCard.layoutParams = cardParams
            binding.localVideoCard.radius = dpToPx(12).toFloat()
            binding.localVideoCard.elevation = dpToPx(8).toFloat()

            binding.tvPipLabel.text = "You"
        }

        // Animate the transition
        val transition = androidx.transition.ChangeBounds().apply { duration = 250 }
        androidx.transition.TransitionManager.beginDelayedTransition(root, transition)
        root.requestLayout()
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()


    private fun startScreenShare() {

        val mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        screenShareLauncher.launch(mediaProjectionManager.createScreenCaptureIntent())
    }

    private fun stopScreenShare() {

        hmsSDK?.stopScreenshare(object : HMSActionResultListener {

            override fun onSuccess() {
                runOnUiThread {
                    isScreenSharing = false
                    updateScreenShareUI(false)
                }
            }

            override fun onError(error: HMSException) {
                runOnUiThread {
                    Toast.makeText(
                        this@VideoCallActivity,
                        "Error stopping share: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    // ── UI state helpers ──────────────────────────────────────

    private fun updateMicUI() {
        if (isMicMuted) {
            binding.btnToggleMic.setImageResource(R.drawable.ic_mic_off)
            binding.btnToggleMic.setBackgroundResource(R.drawable.bg_control_btn_mute)
            binding.btnToggleMic.setColorFilter(android.graphics.Color.parseColor("#EF9A9A"))
        } else {
            binding.btnToggleMic.setImageResource(R.drawable.ic_mic)
            binding.btnToggleMic.setBackgroundResource(R.drawable.bg_control_btn_active)
            binding.btnToggleMic.setColorFilter(android.graphics.Color.WHITE)
        }
    }

    private fun updateCameraUI() {
        if (isCamMuted) {
            binding.btnToggleCamera.setImageResource(R.drawable.ic_videocam_off)
            binding.btnToggleCamera.setBackgroundResource(R.drawable.bg_control_btn_mute)
            binding.btnToggleCamera.setColorFilter(android.graphics.Color.parseColor("#EF9A9A"))
            binding.localVideoView.visibility = android.view.View.INVISIBLE
            binding.tvLocalAvatar.visibility = android.view.View.VISIBLE
        } else {
            binding.btnToggleCamera.setImageResource(R.drawable.ic_video_call)
            binding.btnToggleCamera.setBackgroundResource(R.drawable.bg_control_btn_active)
            binding.btnToggleCamera.setColorFilter(android.graphics.Color.WHITE)
            binding.localVideoView.visibility = android.view.View.VISIBLE
            binding.tvLocalAvatar.visibility = android.view.View.GONE
        }
    }

    private fun updateScreenShareUI(sharing: Boolean) {
        binding.screenShareBanner.visibility = if (sharing) android.view.View.VISIBLE else android.view.View.GONE
        if (sharing) {
            binding.btnScreenShare.setBackgroundResource(R.drawable.bg_control_btn_mute)
            binding.btnScreenShare.setColorFilter(android.graphics.Color.parseColor("#4FC3F7"))
        } else {
            binding.btnScreenShare.setBackgroundResource(R.drawable.bg_control_btn_active)
            binding.btnScreenShare.setColorFilter(android.graphics.Color.WHITE)
        }
    }

    // ── Token & Room ──────────────────────────────────────────

    private fun fetchJoinToken(
        roomId: String,
        userId: String,
        role: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        Log.d("HMS_DEBUG", "Fetching token: roomId=$roomId, userId=$userId, role=$role")

        val body = mapOf("roomId" to roomId, "userId" to userId, "role" to role)

        RetrofitClient.api.getJoinToken(body).enqueue(object : Callback<TokenResponse> {
            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                Log.d("HMS_DEBUG", "Response code: ${response.code()}")
                if (response.isSuccessful && response.body()?.token != null) {
                    onSuccess(response.body()!!.token)
                } else {
                    onFailure("Server ${response.code()}: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.e("HMS_DEBUG", "Network failure: ${t.message}")
                onFailure("Network: ${t.message}")
            }
        })
    }

    private fun joinRoom(authToken: String, userName: String) {
        hmsSDK = HMSSDK.Builder(applicationContext).build()
        val config = HMSConfig(userName = userName, authtoken = authToken)

        hmsSDK?.join(config, object : HMSUpdateListener {

            override fun onJoin(room: HMSRoom) {
                Log.d("HMS", "Joined room: ${room.roomId}")
                runOnUiThread {
                    // Start session timer
                    timerHandler.post(timerRunnable)
                    // Render local video
                    hmsSDK?.getLocalPeer()?.videoTrack?.let { renderLocalVideo(it) }
                }
            }

            override fun onRoomUpdate(type: HMSRoomUpdate, hmsRoom: HMSRoom) {
                // no-op
            }

            override fun onPeerUpdate(type: HMSPeerUpdate, peer: HMSPeer) {
                when (type) {
                    HMSPeerUpdate.PEER_JOINED -> Log.d("HMS", "Peer joined: ${peer.name}")
                    HMSPeerUpdate.PEER_LEFT -> runOnUiThread {
                        binding.remoteVideoView.removeTrack()
                        binding.remoteAvatarContainer.visibility = android.view.View.VISIBLE
                        binding.tvRemoteName.text = "${peer.name} left"
                    }
                    else -> {}
                }
            }

            override fun onTrackUpdate(type: HMSTrackUpdate, track: HMSTrack, peer: HMSPeer) {
                if (peer.isLocal) return
                when {
                    type == HMSTrackUpdate.TRACK_ADDED && track is HMSVideoTrack -> {
                        runOnUiThread {
                            binding.remoteAvatarContainer.visibility = android.view.View.GONE
                            renderRemoteVideo(track)
                        }
                    }
                    type == HMSTrackUpdate.TRACK_REMOVED && track is HMSVideoTrack -> {
                        runOnUiThread {
                            binding.remoteVideoView.removeTrack()
                            binding.remoteAvatarContainer.visibility = android.view.View.VISIBLE
                        }
                    }
                    type == HMSTrackUpdate.TRACK_MUTED && track is HMSVideoTrack -> {
                        runOnUiThread {
                            binding.remoteAvatarContainer.visibility = android.view.View.VISIBLE
                        }
                    }
                    type == HMSTrackUpdate.TRACK_UNMUTED && track is HMSVideoTrack -> {
                        runOnUiThread {
                            binding.remoteAvatarContainer.visibility = android.view.View.GONE
                        }
                    }
                }
            }

            override fun onRoleChangeRequest(request: HMSRoleChangeRequest) {
                // no-op
            }

            override fun onMessageReceived(message: HMSMessage) {
                // no-op
            }

            override fun onError(error: HMSException) {
                Log.e("HMS", "Error: ${error.message}")
                runOnUiThread {
                    Toast.makeText(this@VideoCallActivity, error.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onRemovedFromRoom(notification: HMSRemovedFromRoom) {
                runOnUiThread { finish() }
            }

            override fun onChangeTrackStateRequest(details: HMSChangeTrackStateRequest) {
                // no-op
            }
        })
    }

    private fun renderLocalVideo(track: HMSVideoTrack) {
        localTrack = track
        binding.localVideoView.removeTrack()
        if (isSwapped) {
            binding.remoteVideoView.removeTrack()
            binding.remoteVideoView.addTrack(track)
        } else {
            binding.localVideoView.addTrack(track)
        }
    }

    private fun renderRemoteVideo(track: HMSVideoTrack) {
        remoteTrack = track
        binding.remoteVideoView.removeTrack()
        if (isSwapped) {
            binding.localVideoView.removeTrack()
            binding.localVideoView.addTrack(track)
        } else {
            binding.remoteVideoView.addTrack(track)
        }
    }

    private fun leaveRoom() {
        hmsSDK?.leave(object : HMSActionResultListener {
            override fun onSuccess() { finish() }
            override fun onError(error: HMSException) {
                Log.e("HMS", "Leave failed: ${error.message}")
                finish()
            }
        })
    }

    override fun onDestroy() {
        timerHandler.removeCallbacks(timerRunnable)
        hmsSDK?.leave(null)
        hmsSDK = null
        super.onDestroy()
    }
}