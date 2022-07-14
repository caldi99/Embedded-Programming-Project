package it.francesco.caldivezzi.counters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import android.os.Build
import android.util.Log

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import it.francesco.caldivezzi.counters.database.DataBaseHelper
import java.lang.Exception
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class RestartReceiver : BroadcastReceiver()
{
    /**
     * This Broadcast Receiver Is used when the phone went switch off or restarted
     * */
    override fun onReceive(context: Context, intent: Intent)
    {
        val db : DataBaseHelper =  DataBaseHelper(context)
        val list : List<Long> = db.getAlarm()
        val alarmHelper : AlarmHelper = AlarmHelper(context)

        Toast.makeText(context, "SERVICE PARTITO",Toast.LENGTH_SHORT).show()
        if(!list.isEmpty()) {
            Toast.makeText(context, "lista vuota",Toast.LENGTH_SHORT).show()
            alarmHelper.setAlarm(list[1] - (Calendar.getInstance().timeInMillis - list[0]))
        }
    }
}