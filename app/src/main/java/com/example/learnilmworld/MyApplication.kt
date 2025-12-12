package com.example.learnilmworld

import android.app.Application
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Jitsi Meet
        val serverURL = URL("https://meet.jit.si")
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setFeatureFlag("welcomepage.enabled", false)
            .setFeatureFlag("prejoinpage.enabled", false)
            .setFeatureFlag("recording.enabled", false)
            .setFeatureFlag("live-streaming.enabled", false)
            .setFeatureFlag("pip.enabled", true)
            .setFeatureFlag("chat.enabled", true)
            .setFeatureFlag("invite.enabled", false)
            .setFeatureFlag("android.screensharing.enabled", true)
            .build()

        JitsiMeet.setDefaultConferenceOptions(defaultOptions)
    }
}