package com.example.sleepenvoirmenttracker
import com.example.sleepenvoirmenttracker.ui.theme.SleepSuggestion
import java.time.LocalDateTime

data class SleepSession(val startTime: LocalDateTime, val endTime: LocalDateTime, val averageLight: Float, val averageNoise: Float,
                        val peakNoise: Float, val suggestion: SleepSuggestion)

