package com.github.grishberg.measurementresults

import android.os.SystemClock
import androidx.annotation.MainThread
import com.github.grishberg.measurementresults.data.MeasurementData

private const val INITIAL_CAPACITY = 1024

@MainThread
class MemoryReporter(
    private val bufferSize: Int = INITIAL_CAPACITY,
    private val storage: MeasurementStorage
) : MeasurementReporter {
    private val idBuffer = Array<String>(bufferSize)
    private val threadDuration = LongArray(bufferSize)
    private val microDuration = LongArray(bufferSize)

    private val valuesMap = HashMap<String, MeasurementData>()

    private var index: Int = 0

    override fun addValue(
        measurementName: String,
        threadDurationVal: Long,
        microDurationVal: Long
    ) {
        idBuffer[index] = measurementName
        threadDuration[index] = threadDurationVal
        microDuration[index] = microDurationVal
        index++
    }

    override fun start(eventName: String) {
        val startThreadTime = SystemClock.currentThreadTimeMillis()
        val nanoStartTime = System.nanoTime()

        val startDataHolder = valuesMap[eventName]

        if (startDataHolder == null) {
            valuesMap[eventName] = MeasurementData(eventName, startThreadTime, nanoStartTime)
        } else {
            startDataHolder.name = eventName
            startDataHolder.threadDuration = startThreadTime
            startDataHolder.microDuration = nanoStartTime
        }
    }

    override fun stop(eventName: String) {
        val endThreadTime = SystemClock.currentThreadTimeMillis()
        val endNanoTime = System.nanoTime()
        val startMeasurementData =
            valuesMap[eventName] ?: throw IllegalStateException("Called stop() before start()")

        idBuffer[index] = eventName
        threadDuration[index] = endThreadTime - startMeasurementData.threadDuration
        microDuration[index] = endNanoTime - startMeasurementData.microDuration
        index++

        if (index == bufferSize) {
            storeValues()
        }
    }

    override fun storeValues() {
        val results = mutableListOf<MeasurementData>()
        for (i in 0 until index) {
            results.add(
                MeasurementData(
                    idBuffer[i],
                    threadDuration[i],
                    microDuration[i]
                )
            )
        }
        storage.storeData(results)
        index = 0
    }
}