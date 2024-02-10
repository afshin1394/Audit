package com.irancell.nwg.ios.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.irancell.nwg.ios.R
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit


class SendGpsService : Service() {
    lateinit var pendingIntent: PendingIntent
    companion object {
        const val Notification_ID = 123
        const val CHANNEL_ID = "GPS TRACKER"
        var  isRunning :Boolean = false
    }


    override fun onCreate() {
        super.onCreate()
        isRunning = true
        val notification = createNotification(this@SendGpsService, "Gps Tracking On", "retrieving gps data")
        val intent = Intent(this, SendGpsService::class.java)
        startForeground(Notification_ID, notification);

        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }


    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            Log.i("onHandledIntent", "Thread: " + Thread.currentThread())
            startAlarm()
            scope.cancel()
        }


        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        cancelAlarm()
        NotificationManagerCompat.from(this).cancelAll()
        stopSelf()
        isRunning = false
    }

    private fun startAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val currentTimeMillis = SystemClock.elapsedRealtime()
        val intervalMillis = TimeUnit.SECONDS.toMillis(5)

        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            currentTimeMillis + intervalMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    @SuppressLint("MissingPermission")
    fun createNotification(
        context: Context,
        title: String,
        content: String,
    ) : Notification {
        createNotificationChannel(title, content)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_yellow)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification = builder.build()
        with(NotificationManagerCompat.from(this)) {

            Log.i("notification", "createNotification: ")
            notify(Notification_ID, notification)
            Log.i("notification", "createNotification  accepted: ")


        }
        return notification
    }


    private fun createNotificationChannel(name: String, content: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = content
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



}