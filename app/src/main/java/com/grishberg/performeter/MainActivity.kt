package com.grishberg.performeter

import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.grishberg.performeter.samples.JavaSample1
import com.grishberg.performeter.samples.JavaSample2
import com.grishberg.performeter.samples.KotlinSample1
import com.grishberg.performeter.samples.KotlinSample2

private const val TAG = "Performeter"
// --es
private const val MODE_EXTRA = "mode"
// --ei
private const val ITERATIONS_EXTRA = "iterations"
private const val EXPERIMENT_NUMBER_EXTRA = "number"

private const val ITERATIONS_COUNT = 500000

/**
 * --es mode "k1"
 * --es mode "j1"
 * --es mode "k2"
 * --es mode "j2"
 */
class MainActivity : AppCompatActivity() {
    private lateinit var reporter: ResultReporter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reporter = LogReporter()

        launchPerfTests()
    }

    private fun launchPerfTests() {
        val mode = intent.getStringExtra(MODE_EXTRA)
        val iterations = intent.getIntExtra(ITERATIONS_EXTRA, ITERATIONS_COUNT)
        val experimentNumber = intent.getIntExtra(EXPERIMENT_NUMBER_EXTRA, 0)
        val runnable: RunnableWithResult
        when (mode) {
            "k1" -> {
                runnable = KotlinSample1()
            }
            "k2" -> {
                runnable = KotlinSample2()
            }
            "j1" -> {
                runnable = JavaSample1()
            }
            else -> {
                runnable = JavaSample2()
            }
        }
        start(runnable, iterations, experimentNumber)
    }

    private fun start(
        runnable: RunnableWithResult,
        iterations: Int,
        experimentNumber: Int
    ) {
        // single micro benchmark with nano
        runnable.init()

        val microStartTime = System.nanoTime()
        runnable.run()
        val microDuration = System.nanoTime() - microStartTime

        // main benchmark
        runnable.init()
        var totalThreadTime = 0L
        for (i in 0 until iterations) {
            val startThreadTime = SystemClock.currentThreadTimeMillis()
            runnable.run()
            totalThreadTime += SystemClock.currentThreadTimeMillis() - startThreadTime
        }

        reporter.reportPerfResults(
            experimentNumber,
            totalThreadTime,
            microDuration
        )

        Log.e(TAG, "result_$experimentNumber = " + runnable.result)
    }
}
