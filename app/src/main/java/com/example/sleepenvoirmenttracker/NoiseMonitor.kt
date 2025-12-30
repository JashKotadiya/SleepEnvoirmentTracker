package com.example.sleepenvoirmenttracker

import android.media.MediaRecorder
import java.io.File
import kotlin.math.log10

class NoiseMonitor(private val context: android.content.Context) {
    private var recorder: MediaRecorder? = null

    fun start() {
        stop()
        val audioFile = File(context.cacheDir, "temp_audio.3gp")
        if (!audioFile.exists()) {
            audioFile.createNewFile()
        }
        recorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            MediaRecorder(context)
        }
        else {
            // Inside trailing lambda syntax, the scope is assumed to be the object we just created
            MediaRecorder()
            }
        recorder?.apply {
            try {
                setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                val outFile = "${context.cacheDir.absolutePath}/temp_audio.3gp"
                setOutputFile(outFile)
                prepare()
                start()
            }
            catch (e: Exception) {
                android.util.Log.e("NoiseMonitor", "Failed to start recorder: ${e.message}")
            }
        }
    }

    fun stop() {
        recorder?.stop()
        recorder?.release()
        recorder = null
    }

    fun getDecibels(): Float {
        val amplitude = recorder?.maxAmplitude ?: 0
        return if (amplitude > 0)
//            (20 * log10(amplitude.toDouble())).toFloat()
            (20 * kotlin.math.log10(amplitude.toDouble())).toFloat()
        else 0f
    }

}