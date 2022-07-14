package it.francesco.caldivezzi.counters

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import it.francesco.caldivezzi.counters.database.AppConstants

@RequiresApi(Build.VERSION_CODES.O)
class NotificationHelper(base: Context) :ContextWrapper(base)
{
    private var mManager :NotificationManager? = null

    init
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel()
    }

    /**
     * This function create a notification channel.
     * */
    fun createChannel()
    {
        val channel : NotificationChannel = NotificationChannel(AppConstants.CHANNEL_ID, AppConstants.CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = R.color.white
        channel.lockscreenVisibility =Notification.VISIBILITY_PRIVATE
        getManager().createNotificationChannel(channel)
    }


    /**
     * This function return the notificationManager with which can build Notification
     * */
    fun  getManager() : NotificationManager
    {
        if(mManager == null)
        {
            mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager as NotificationManager
    }


    /**
     * This function give a name and a message, creates a notification.
     * */
    fun getChanell(title:String,message : String) : NotificationCompat.Builder
    {

        val snoozeIntent = Intent(this, RemoveNotificationReceiver::class.java).apply {
            action = AppConstants.ACTION_DELETE_TIMER
            putExtra(Notification.EXTRA_NOTIFICATION_ID, 0)
        }
        val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent, 0)

        return NotificationCompat.Builder(applicationContext, AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_timer)
            .setOngoing(true)
            .addAction(R.drawable.ic_notification_stop_button, getString(R.string.notification_stop_timer),
                snoozePendingIntent)
    }

    /**
     * This function destroy the channel
     * */
    fun removeChannel()
    {
        mManager?.deleteNotificationChannel(AppConstants.CHANNEL_ID)
    }

}