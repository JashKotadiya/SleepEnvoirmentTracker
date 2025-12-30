package com.example.sleepenvoirmenttracker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

// The : after the constructor means implements, so this class implements SensorEventListener
class LightSensorManager(context: Context, private val repository: SleepRepository, ) : SensorEventListener {
    // Context is the way to get system services
    // Context.SENSOR_SERVICE is a final string which is the name
    // as SensorManager casts the return of getSystemService() to SensorManager because getSystemService returns an Any? object
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    fun start() {
        // This let syntax means that if the light sensor exists, take a snapshot of it and call it "it", and pass that as the arguement for the sensor
        // The ? checks if lightSensor is null, and if it is not, "it" becomes a reference to the valid lightSensor
        // You could do this code with an if statement but we dont know if between the checking of the if statement and the execuation of registerListener() if lightSensor becomes null
        // but let solves this because even if lightSensor becomes null, "it" still holds a reference to the previous valid light sensor
        lightSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Ambient light level
        val lux = event.values[0]
        repository.updateLight(lux)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("Hello World")
    }

}