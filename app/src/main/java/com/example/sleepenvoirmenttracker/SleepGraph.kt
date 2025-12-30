package com.example.sleepenvoirmenttracker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtMost

@Composable
fun SleepGraph(readings: List<SleepReading>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text("Environmental Trends", style = MaterialTheme.typography.labelMedium)
        Canvas(modifier = Modifier.weight(1f).fillMaxSize().padding(top = 8.dp)) {
            // Canvas takes a lambda as its arguement and return@Canvas means to just exit the lambda and not the entire function
            if (readings.size < 2) return@Canvas
            // size.height and size.width are the height and width of the canvas
            val width = size.width
            val height = size.height
            val maxReadings = readings.size
            val maxVal = 100F

            // Path() is for drawing like a pen stroke
            val lightPath = Path()
            val noisePath = Path()

            readings.forEachIndexed { index, reading ->
                val x = (index.toFloat() / (maxReadings - 1) * width)
                val lightY =
                    height - (reading.lightLevel.fastCoerceAtMost(maxVal) / maxVal) * height
                val noiseY =
                    height - (reading.noiseLevel.fastCoerceAtMost(maxVal) / maxVal) * height

                if (index == 0) {
                    // moveTo() starts the path
                    lightPath.moveTo(x, lightY)
                    noisePath.moveTo(x, noiseY)
                } else {
                    // lineTo() draws a line from previous point to current point
                    lightPath.lineTo(x, lightY)
                    noisePath.lineTo(x, noiseY)
                }

            }

            drawPath(path = noisePath, color = Color(0xFFE57373), style = Stroke(width = 4f))

            drawPath(path = lightPath, color = Color(0xFFFFD54F), style = Stroke(width = 4f))
        }


        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "● Light (Lux)",
                color = Color(0xFFFFD54F),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "● Noise (dB)",
                color = Color(0xFFE57373),
                style = MaterialTheme.typography.bodySmall
            )
        }


    }
}
