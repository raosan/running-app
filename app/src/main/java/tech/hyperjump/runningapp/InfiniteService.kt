package tech.hyperjump.runningapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import java.lang.IllegalStateException

const val CHANNEL_ID = "NotificationChannel"
const val CHANNEL_NAME = "BestChannelEver"
const val FOREGROUND_ID= 1

const val FETCH_DATA_INTERVAL = 0

const val  ACTION_START = "ACTION_START"
const val  ACTION_STOP = "ACTION_STOP"

class InfiniteService: Service() {
    var isServiceRunning = false
    lateinit var  wakeLock: PowerManager.WakeLock

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
            else -> throw IllegalStateException("Action can onle be $ACTION_START or $ACTION_STOP")
        }
        return START_STICKY
    }

    private fun stop() {
        TODO("Not yet implemented")
    }

    private fun start() {
        TODO("Not yet implemented")
    }
}