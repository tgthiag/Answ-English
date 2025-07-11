package AnsweringAPP.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.AnsweringAPP.R

class ScreenRecordingService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Do not start the recording logic directly from the service
        return START_NOT_STICKY
    }

    private fun startForegroundService() {
        val notificationId = 1
        val channelId = "ScreenRecordingChannel"
        val channelName = "Screen Recording"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Screen Recording")
            .setContentText("Screen recording in progress")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(notificationId, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        this.stopForeground(true)
    }
}

