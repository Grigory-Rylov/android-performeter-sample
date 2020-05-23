package com.github.grishberg.measurementresults.data

data class StartMeasurementData(
    val name: String,
    val startThreadTime: Long,
    val startNanoTime: Long
)