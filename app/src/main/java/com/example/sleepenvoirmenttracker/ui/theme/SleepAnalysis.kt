package com.example.sleepenvoirmenttracker.ui.theme

import android.hardware.lights.Light
import androidx.compose.ui.graphics.Color
import com.example.sleepenvoirmenttracker.SleepReading
import kotlin.math.roundToInt

data class SleepSuggestion(val message: String, val color: Color)

// object is a class that you can only create one instance of and Kotlin automatically creates the one instance for you
object SleepAnalysis {
    fun analyzeSession(readings: List<SleepReading>): SleepSuggestion {
        if (readings.isEmpty()) {
            return SleepSuggestion("No Data Collected Yet. ", Color.Gray)
        }
        val avgLight = readings.map { it.lightLevel }.average().toFloat()
        val avgNoise = readings.map { it.noiseLevel }.average().toFloat()
        val peakNoise = readings.maxOfOrNull { it.noiseLevel } ?: 0f

        val lightMessage = getLightSummary(avgLight)
        val noiseMessage = getNoiseSummary(avgNoise, peakNoise)

        val isBadNight = avgLight > 20 || avgNoise > 50

        val color = if (isBadNight) Color(0xFFE57373) else Color(0xFF81C784)

        return SleepSuggestion(
            message = "Sleep Report:\n" +
                    "• Average Light: ${avgLight.roundToInt()} lux\n" +
                    "• Average Noise: ${avgNoise.roundToInt()} dB\n\n" +
                    "$lightMessage\n$noiseMessage",
            color = color
        )
    }

    fun generateReport(avgLight: Float, avgNoise: Float, peakDb: Float): SleepSuggestion {
        val lightMessage = getLightSummary(avgLight)
        val noiseMessage = getNoiseSummary(avgNoise, peakDb)

        val isBadNight = avgLight > 20 || avgNoise > 50

        val color = if (isBadNight) Color(0xFFE57373) else Color(0xFF81C784)

        return SleepSuggestion(
            message = "Sleep Report:\n" +
                    "• Average Light: ${avgLight.roundToInt()} lux\n" +
                    "• Average Noise: ${avgNoise.roundToInt()} dB\n\n" +
                    "$lightMessage\n$noiseMessage",
            color = color
        )
    }
}
private fun getLightSummary(avgLux: Float): String {
    return when {
        avgLux < 5 -> "🌑 Darkness was perfect for sleep."
        avgLux < 20 -> "🌖 Room was slightly dim. Try blackout curtains."
        else -> "💡 Room was too bright ($avgLux avg). This suppresses melatonin."
    }
}

private fun getNoiseSummary(avgDb: Float, peakDb: Float): String {
    val baseMsg = when {
        avgDb < 40 -> "🔇 Noise levels were healthy."
        else -> "🔊 Background noise was high ($avgDb dB)."
    }

    return if (peakDb > 70) {
        "$baseMsg (Spike detected at ${peakDb.toInt()} dB)"
    } else {
        baseMsg
    }
}
