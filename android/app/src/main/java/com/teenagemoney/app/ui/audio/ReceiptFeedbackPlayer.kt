package com.teenagemoney.app.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.teenagemoney.app.R

/** Plays the one-shot confirmation for a successfully persisted expense. */
class ReceiptFeedbackPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(AudioManager::class.java)
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundId: Int = soundPool.load(appContext, R.raw.receipt_clink, 1)
    private var loaded = false
    private var pendingPlay = false

    init {
        soundPool.setOnLoadCompleteListener { _, loadedSoundId, status ->
            if (loadedSoundId != soundId || status != 0) return@setOnLoadCompleteListener
            loaded = true
            if (pendingPlay) {
                pendingPlay = false
                playSoundIfAudible()
            }
        }
    }

    fun play(enabled: Boolean) {
        if (!enabled || !isAudible()) return
        if (!loaded) {
            // The database write already succeeded; play once as soon as the local asset is ready.
            pendingPlay = true
            return
        }
        playSoundIfAudible()
    }

    private fun playSoundIfAudible() {
        if (!isAudible()) return
        soundPool.play(soundId, 0.42f, 0.42f, 1, 0, 1.0f)
        vibrateOnce()
    }

    private fun isAudible(): Boolean =
        audioManager?.ringerMode == AudioManager.RINGER_MODE_NORMAL &&
            audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) > 0

    private fun vibrateOnce() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = appContext.getSystemService(VibratorManager::class.java)
                manager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(16L, 45)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = appContext.getSystemService(Vibrator::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(16L, 45))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(16L)
                }
            }
        } catch (_: Exception) {
            // Haptics must never affect the saved transaction or receipt.
        }
    }

    fun release() {
        pendingPlay = false
        soundPool.setOnLoadCompleteListener(null)
        soundPool.release()
    }
}
