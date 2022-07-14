package it.francesco.caldivezzi.counters

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import it.francesco.caldivezzi.counters.database.AppConstants

class AlertReceiver : BroadcastReceiver()
{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?)
    {
        val notificationHelper : NotificationHelper = NotificationHelper(context!!)
        val nb :  NotificationCompat.Builder = notificationHelper.getChanell(context.getString(R.string.notification_title),context.getString(R.string.notification_info))
        notificationHelper.getManager().notify(AppConstants.ID_NOTIFICATION,nb.build())
    }

}