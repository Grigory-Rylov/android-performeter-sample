package com.grishberg.performeter

import android.util.Log

/**
 * Result reporter interface
 */
interface ResultReporter {
    fun reportPerfResults(
        experimentNumber: Int,
        threadDuration: Long,
        microDuration: Long
    )
}

/**
 * Log reporter.
 */
class LogReporter : ResultReporter {
    override fun reportPerfResults(
        experimentNumber: Int,
        threadDuration: Long,
        microDuration: Long
    ) {
        Log.e("[PERF_$experimentNumber]", "(td=$threadDuration, md=$microDuration)")
    }
}
