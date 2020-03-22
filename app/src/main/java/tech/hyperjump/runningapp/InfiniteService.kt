package tech.hyperjump.runningapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import java.lang.IllegalStateException

const val CHANNEL_ID = "NotificationChannel"
const val CHANNEL_NAME = "BestChannelEver"
const val FOREGROUND_ID= 1
const val REQUEST_CODE_NOTIFICATION = 0

const val FETCH_DATA_INTERVAL = 200L // every 2 second

const val  ACTION_START = "ACTION_START"
const val  ACTION_STOP = "ACTION_STOP"

class InfiniteService: Service() {
    var isServiceRunning = false
    lateinit var  wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val mapsActivityIntent = Intent(this, MapsActivity::class.java).let {
            PendingIntent.getActivity(this, REQUEST_CODE_NOTIFICATION, it, 0)
        }

        startForeground(FOREGROUND_ID, getNotification(mapsActivityIntent))
    }

    private fun getNotification(pendingIntent: PendingIntent) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Running App")
            .setContentText(("Tracking your run"))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "myDescription"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
            else -> throw IllegalStateException("Action can only be $ACTION_START or $ACTION_STOP")
        }
        return START_STICKY
    }

    private fun stop() {
        if (!isServiceRunning) return
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        stopForeground(true)
        stopSelf()
        isServiceRunning = false
        Log.d("infiniteService", "Service stopped")
    }

    private fun start() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "InfiniteService::wakelock")
            .apply { acquire() }

        startTrackingRun(FETCH_DATA_INTERVAL)

        Log.d("infiniteService", "Service started")
        isServiceRunning = true
    }

    private fun startTrackingRun(fetchDataInterval: Long) {
        TODO("Not yet implemented")
    }
}