package com.example.sleepenvoirmenttracker
import java.time.LocalDateTime
import java.time.LocalTime

// Note that data tells Kotlin to automatically generate equals(), hashCode(), toString(), and copy() method automatically
// The constructor in Kotlin is in the class header, so class SleepReading(), SleepReading() is the constructor
// val is the equivalnt of final in java

// Currently represents a single sensor reading at a point in time
data class SleepReading(
    val timestamp: LocalDateTime,
    val lightLevel: Float,
    val noiseLevel: Float
)