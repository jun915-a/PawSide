package com.pawside.app.data

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager

/**
 * Maps the device's live system status to the dog mood shown on the widget.
 *
 * Priority (highest first):
 *  1. charging  -> [DogState.CHARGING]      (happily asleep)
 *  2. memory hi -> [DogState.MEMORY_HIGH]   (sleepy, sitting)
 *  3. otherwise -> [DogState.BATTERY_GOOD]  (running around)
 */
object StatusEvaluator {

    /** Percent of total memory in use above which pressure is considered "high".
     *  Set high so the state only triggers under genuine pressure; not user-tunable. */
    private const val MEMORY_THRESHOLD_PERCENT = 99

    fun evaluate(context: Context): DogState = when {
        isCharging(context) -> DogState.CHARGING
        isMemoryHigh(context, MEMORY_THRESHOLD_PERCENT) -> DogState.MEMORY_HIGH
        else -> DogState.BATTERY_GOOD
    }

    private fun isCharging(context: Context): Boolean {
        val batteryManager = context.getSystemService(BatteryManager::class.java)
        return batteryManager?.isCharging == true
    }

    private fun isMemoryHigh(context: Context, thresholdPercent: Int): Boolean {
        val activityManager = context.getSystemService(ActivityManager::class.java) ?: return false
        val info = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(info)
        if (info.lowMemory) return true
        if (info.totalMem <= 0L) return false
        val usedRatio = 1.0 - info.availMem.toDouble() / info.totalMem.toDouble()
        return usedRatio >= thresholdPercent / 100.0
    }
}
