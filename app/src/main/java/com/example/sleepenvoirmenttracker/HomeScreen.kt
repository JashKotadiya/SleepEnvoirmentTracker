package com.example.sleepenvoirmenttracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sleepenvoirmenttracker.ui.theme.SleepAnalysis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
@RequiresApi(Build.VERSION_CODES.O) // API 26
@Composable
fun HomeScreen(repository: SleepRepository, lightSensorManager: LightSensorManager, noiseMonitor: NoiseMonitor, isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    // listOf creates an immutable list
    val tabs = listOf("Tracker", "History")

    // Scaffold provides specfic parameters to easily insert components like TopBar, BottomBar, Content, FloatingActionButton without you having to
    // manually calculate their position relative to your main content
    Scaffold(
        // topBar wants a lambda so you can pass whatever you want inside it and Kotlin executes it when it needs to draw the topBar
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                // TabRow is a Composable function which lays out tabs horizontally and highlights the selected tab
                TabRow(selectedTabIndex = selectedTab, modifier = Modifier.statusBarsPadding()) {
                    // this is all the tabs that go inside the row
                    // forEachIndexed() loops over a list and gives us the index of the item and the item
                    // title -> ... , title is the param and after the -> is the function body
                    // Tab() creates one Tab
                    // onClick works because it takes in a lambda and runs later when the tab is clicked
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTab == 0) "Sleep Tracker" else "Sleep History",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick ={onThemeChange(!isDarkTheme)}) {
                        Icon(
                            // This uses Material Icons - make sure you have the dependency
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
                        )
                    }
                }
            }
        }


    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (selectedTab == 0) {
                TrackerTab(repository, lightSensorManager, noiseMonitor)
            } else {
                HistoryTab(repository.sessionHistory)
            }
        }
    }
}

@Composable
fun TrackerTab(repository: SleepRepository, lightSensorManager: LightSensorManager, noiseMonitor: NoiseMonitor ) {

    // Unit is Kotlin's version of void, when the screen is first loaded Compose looks at the key and since it hasnt run this effect yet it runs it
    // Now every time the UI is recomposed, if the key has changed then the effect is run again, since in this Unit is always Unit this effect is only run once
    // onDispose() only runs when the component is removed from the UI and is for cleanup
    DisposableEffect(Unit) {
        onDispose {
            lightSensorManager.stop()
            noiseMonitor.stop()
        }
    }

    val readings = repository.readings
    // For the below value we want screen to redraw every time the isTracking value updates, so we wrap isTracking in a mutableStateOf object which
    // returns an object which compose listens to, when the value changes inside mutableStateOf, every function using that value is run again automatically
    // the remember part allocates some memory for mutableStateOf object because, in Compose every time we wanna redraw the UI, the Composable functions are run from top to bottom
    // so if we have a value stored for isTracking, Compose will look there instead of using the default value
    // If we did not remember the value, each time this function would be run the default value of isTracking would be used instead
    // remember tells compose to calculate the value the first time the screen is drawn but otherwise it gets it from its saved place in memory
    // The by operator tells Kotlin when ask for isTracking go inside mutableStateOf object and get it yourself
    var isTracking by remember { mutableStateOf(repository.isTracking) }
    // The below allows us to run background tasks inside a composable function, without them duplicating or restarting everytime the UI is updated
    // If you ran a background task normally, every time the UI would update, the background task would start again
    // CoroutineScope() gives you a save envoirnment to launch background tasks for a Composable function
    // It provides the same scope everytime the UI is redrawn and it cancels automatically if the user leaves the screen
    val coroutineScope = rememberCoroutineScope()

    val showReport = !isTracking && readings.isNotEmpty()


    // The column stacks what ever is inside it vertically
    // The modifier here first takes up as much space as it's parent allows, and then adds 16dp of padding
    // Arrangement.spacedBy(16.dp) describes how all of its children should be spaced -> the children are what is passed to the content arguement
    // it doesnt apply spacing but gives us a spacing objects which knows how to apply spacing
    // The {} after each function is Kotlin's trailing lambda syntax, we have an anon function with param content and inside the function body we have text and style and more
    // the anon function is called by Compose when need to render this part of the UI

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // MaterialTheme provides color schemes, typography, and shapes -
//        Text(text = "Sleep Environment Tracker", style = MaterialTheme.typography.headlineMedium)
        // latest will grab the latest data from readings or if there is none, set latest to null
        val latest: SleepReading? = readings.lastOrNull()
        // The Card class expects to recieve a set of instructions for colors which tells it how to behave in different states
        // CardDefaults is a helper class with helper functions, cardColors() generates the instruction set for Card, and lets you override the colors that you want
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // {} here are to put code inside a string
                // latest?.lightlevel, says only grab the property if it exists, otherwise set to null
                // ?: is the elvis operator, and says if lightLevel is null, use "--" instead of null
                Text("Light: ${latest?.lightLevel ?: "--"} lux")
                Text("Noise: ${latest?.noiseLevel ?: "--"} dB")
                Text("Data Points: ${readings.size}")
            }
        }

        if (readings.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                SleepGraph(
                    readings = readings,
                    modifier = Modifier.padding(16.dp).fillMaxSize()
                )
            }
        }

        if (showReport) {
            val sessionAnalysis = SleepAnalysis.analyzeSession(readings)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = sessionAnalysis.color)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🌙 Morning Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = sessionAnalysis.message)
                }
            }
        }

        Button(
            onClick = {
                isTracking = !isTracking
                if (isTracking) {
                    repository.startTracking()
                    lightSensorManager.start()
                    noiseMonitor.start()
                    val db = noiseMonitor.getDecibels()
                    repository.updateNoise(db)
                    coroutineScope.launch {
                        while (repository.isTracking) {
                            val db = noiseMonitor.getDecibels()
                            repository.updateNoise(db)
                            repository.recordSnapshot()
                            delay(1000L)
                        }
                    }
                } else {
                    repository.stopTracking()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = if (isTracking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        )
        {
            Text(if (isTracking) "Stop Tracking" else "Start Tracking")
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O) // API 26
@Composable
fun HistoryTab(history: List<SleepSession>) {
    if (history.isEmpty()) {
        Text("No sleep history yet. Complete a session to see it here!")
    } else {
        // LazyColumn on creates the items on the screen and when you scroll old items are deleted and new ones are rendered (a regular Column creates everything every single time the UI reloads)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // items() is a helper function for LazyColumn which takes a list of items to render
            // and the lambda arguement defines how to draw one item
            items(history) { session ->
                // alpha is transparency
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = session.suggestion.color.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm")
                        Text(
                            text = "${session.startTime.format(formatter)} - ${
                                session.endTime.format(
                                    formatter
                                )
                            }",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        // A horizontal line which seperates content
                        HorizontalDivider(Modifier.padding(vertical = 4.dp))
                        Text("Avg Light: ${session.averageLight.toInt()} lux")
                        Text("Avg Noise: ${session.averageNoise.toInt()} dB")
                        Text(
                            text = session.suggestion.message,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                }
            }
        }
    }
}
