package com.github.grishberg.measurementresults

import com.github.grishberg.measurementresults.data.MeasurementData

interface MeasurementStorage {
    fun storeData(data: List<MeasurementData>)
}