package com.github.grishberg.measurementresults

import androidx.annotation.MainThread

@MainThread
interface MeasurementReporter {
    fun addValue(
        measurementName: String,
        threadDurationVal: Long,
        microDurationVal: Long
    )

    fun start(eventName: String)
    fun stop(eventName: String)

    fun storeValues()
}