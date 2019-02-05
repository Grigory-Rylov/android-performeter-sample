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
private const val MODE_EXTRA = "mode"
private const val ITERATIONS_COUNT = 500000

/**
 * --es mode "k vs k"
 * --es mode "j vs k"
 * --es mode "k vs j"
 * --es mode "j vs j"
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
        val runnable1: RunnableWithResult
        val runnable2: RunnableWithResult
        when (mode) {
            "k vs k" -> {
                runnable1 = KotlinSample1()
                runnable2 = KotlinSample2()
            }
            "k vs j", "j vs k" -> {
                runnable1 = JavaSample1()
                runnable2 = KotlinSample2()
            }
            else -> {
                runnable1 = JavaSample1()
                runnable2 = JavaSample2()
            }
        }
        start(runnable1, runnable2)
    }

    private fun start(runnable1: RunnableWithResult, runnable2: RunnableWithResult) {
        // single micro benchmark

        val microStartTime1 = System.nanoTime()
        runnable1.run()
        val microDuration1 = System.nanoTime() - microStartTime1

        val microStartTime2 = System.nanoTime()
        runnable2.run()
        val microDuration2 = System.nanoTime() - microStartTime2

        // main benchmark

        val startTime = SystemClock.uptimeMillis()
        val startThreadTime = SystemClock.currentThreadTimeMillis()

        for (i in 0 until ITERATIONS_COUNT) {
            runnable1.run()
        }
        val duration1 = SystemClock.uptimeMillis() - startTime
        val threadDuration1 = SystemClock.currentThreadTimeMillis() - startThreadTime

        val startTime2 = SystemClock.uptimeMillis()
        val startThreadTime2 = SystemClock.currentThreadTimeMillis()
        for (i in 0 until ITERATIONS_COUNT) {
            runnable2.run()
        }
        val duration2 = SystemClock.uptimeMillis() - startTime2
        val threadDuration2 = SystemClock.currentThreadTimeMillis() - startThreadTime2

        reporter.reportPerfResults(
            duration1, duration2,
            threadDuration1, threadDuration2,
            microDuration1, microDuration2
        )

        Log.e(TAG, "result1 = " + runnable1.result + ", result2 = " + runnable2.result)
    }
}
