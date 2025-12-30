package com.example.sleepenvoirmenttracker

import android.content.Context
import android.util.Log
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.sleepenvoirmenttracker.ui.theme.SleepAnalysis
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime

data class SavedSession(val startTime: String, val endTime: String, val avgLight: Float, val avgNoise: Float, val peakNoise: Float)
class SleepRepository(context: Context) {
    // this is in the format of name: type = value
    // mutableStateListOf() is a special list provided by Compose, which notifies the Compose UI whenever the list
    // changes, so updating the list updates the UI automatically
    // mutableStateListOf() returns a SnapshotStateList<T>  which is a list that is tracked by Compose's state system, any changes will trigger UI updates
    val readings: SnapshotStateList<SleepReading> = mutableStateListOf()
    val sessionHistory: SnapshotStateList<SleepSession> = mutableStateListOf()

    // By default for a var Kotlin generates a getter and a setter, which are usually public, saying private set makes the them private
    var isTracking: Boolean = false
            private set

    private var currentLight = 0f
    private var currentNoise = 0f
    private var sessionStartTime: LocalDateTime? = null

    // SharedPreferences is key value storage built into android, MODE_PRIVATE means only the app can read or write to it
    // the key value storage is an xml file
    // calling the get function either creates or opens a file called sleep_tracker_data
    private val prefs = context.getSharedPreferences("sleep_tracker_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        loadHistory() // Load data as soon as the app starts
    }

    fun startTracking() {
        readings.clear()
        sessionStartTime = LocalDateTime.now()
        isTracking = true
    }
    fun stopTracking() {
        if (!isTracking) return
        val analysis = SleepAnalysis.analyzeSession(readings)
        // map is a function which transforms a list into another list, "it" is a reference to the current element, so what these lines are doing are
        // creating a list with only the lightLevel or noiseLevel then taking the average of that and then converting to float
        val avgLight = readings.map {it.lightLevel}.average().toFloat()
        val avgNoise = readings.map { it.noiseLevel }.average().toFloat()
        val peakNoise = readings.maxOfOrNull { it.noiseLevel } ?: 0f

        val session = SleepSession(
            startTime = sessionStartTime ?: LocalDateTime.now(), endTime = LocalDateTime.now(), averageLight = avgLight,
            averageNoise = avgNoise, suggestion = analysis, peakNoise = peakNoise)

        sessionHistory.add(0, session)
        isTracking = false
        saveHistory()
    }

    fun updateLight(lux: Float) {
         currentLight = lux

    }
    fun updateNoise(db: Float) {
        currentNoise = db
    }
    fun recordSnapshot() {
        if (!isTracking) return
        Log.d("TRACKER", "Saving: L=$currentLight, N=$currentNoise")
        readings.add(SleepReading(LocalDateTime.now(), currentLight, currentNoise))
    }

    fun saveThemePreference(isDark: Boolean) {
        prefs.edit().putBoolean("dark_mode_pref", isDark).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean("dark_mode_pref", false)
    }

    private fun saveHistory() {
        val savedList = sessionHistory.map {SavedSession(startTime = it.startTime.toString(), endTime = it.endTime.toString(), avgLight = it.averageLight, avgNoise = it.averageNoise, peakNoise = it.peakNoise)}
        val json = gson.toJson(savedList)
        // We edit SharedPreferences, with the key history.json and the value of json
        // the apply() function runs this async
        prefs.edit().putString("history_json", json).apply()
    }

    private fun loadHistory() {
        // We get the exact text that was stored as a json object in this case
        val json = prefs.getString("history_json", null) ?: return
        // Here we are creating a subclass which inherits from TypeToken<SavedSession>, and the purpose of TypeToken is to save the type of Generics, because while the parent's Generics will be erased
        // a subclass always remembers the parent's type even if it is Generic
        // It also provides a type field which gives the type to gson in a way that it can understand
        val type = object: TypeToken<List<SavedSession>>() {}.type
        // This tells gson to parse the JSON into a list of SavedSession objects and store it in a savedList object
        val savedList: List<SavedSession> = gson.fromJson(json, type)

        savedList.forEach {saved ->
            val report = SleepAnalysis.generateReport(saved.avgLight, saved.avgLight, saved.peakNoise)
            val fullSession = SleepSession(startTime = LocalDateTime.parse(saved.startTime), endTime = LocalDateTime.parse(saved.endTime), averageLight = saved.avgLight, averageNoise = saved.avgNoise, peakNoise = saved.peakNoise , suggestion = report)
            sessionHistory.add(fullSession)
        }
    }
}