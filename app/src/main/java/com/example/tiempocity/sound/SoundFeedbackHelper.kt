package com.example.tiempocity.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator

object SoundFeedbackHelper {
    private var toneGenerator: ToneGenerator? = null

    fun playClickSound(context: Context) {
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
            }
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        } catch (_: Exception) { }
    }

    fun playAcknowledgeBeep(context: Context) {
        try {
            if (toneGenerator == null) {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            }
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 45)
        } catch (_: Exception) { }
    }
}
