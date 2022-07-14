package it.francesco.caldivezzi.counters

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import it.francesco.caldivezzi.counters.database.AppConstants
import it.francesco.caldivezzi.counters.database.DataBaseHelper


class RemoveNotificationReceiver :BroadcastReceiver()
{
    /*
    This function is activated when, the user press on the delete button of the notification
    * */

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?)
    {
        val db : DataBaseHelper =  DataBaseHelper(context)
        db.deleteAlarm()
        val ns = Context.NOTIFICATION_SERVICE
        val manager : NotificationManager = context!!.getSystemService(ns) as NotificationManager
        manager.deleteNotificationChannel(AppConstants.CHANNEL_ID)
    }
}