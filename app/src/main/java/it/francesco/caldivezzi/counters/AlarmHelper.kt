package it.francesco.caldivezzi.counters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import it.francesco.caldivezzi.counters.database.AppConstants
import java.util.*

class AlarmHelper(val context: Context)
{
    /**
     * This function create a alarm.
     * Set the trigger to the timeToWait value (in milliseconds)
     * */
    fun setAlarm(timeToWait: Long)
    {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, AppConstants.REQUEST_CODE, intent, AppConstants.FLAG)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + timeToWait,
            pendingIntent
        )
    }

    /**
     * This function delete an alarm if there was one set.
     * */
    fun cancelAlarm()
    {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, AppConstants.REQUEST_CODE, intent, AppConstants.FLAG)
        alarmManager.cancel(pendingIntent)
    }
}