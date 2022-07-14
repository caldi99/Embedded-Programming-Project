package it.francesco.caldivezzi.counters.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.lang.Exception
import java.sql.Time
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * This class is used by TimerActivity and RestartReceiver to save data about the Alarm set.
 * This class was created for allowing the Broadcast Receiver to access 2 important data :
 * The absolute time when the timer was set and, how many milliseconds are remaining.
 * */
class DataBaseHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    val CREATE_TIMER_STATE = (
            "CREATE TABLE " + timerStateTable + " (" +
                    idState + " INTEGER PRIMARY KEY, " +
                    timeStartAlarm + " INTEGER," +
                    timeUntilEnd + " INTEGER );"
            )

    val QUERY_GET_TIMESTARTALARM_TIMEULTILEND = (
            "SELECT $timeStartAlarm , $timeUntilEnd " +
                    "FROM $timerStateTable " +
                    "WHERE $idState = $ID_RECORD"
            )

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TIMER_STATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + timerStateTable)
        onCreate(db)
    }

    fun insertAlarm(startAlarm: Long, waitTime: Long) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(timeUntilEnd, waitTime)
        values.put(timeStartAlarm, startAlarm)
        values.put(idState, ID_RECORD)
        db.insert(timerStateTable, null, values)
        db.close()
    }

    fun updateAlarm(waitTime: Long) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(timeUntilEnd, waitTime)
        db.update(timerStateTable, values, "$idState=?", arrayOf(ID_RECORD.toString()))
        db.close()
    }

    fun deleteAlarm() {
        val db = writableDatabase
        db.delete(timerStateTable, "$idState=?", arrayOf(ID_RECORD.toString()))
        db.close()
    }

    fun getAlarm(): List<Long> {
        val db = readableDatabase
        val ret = ArrayList<Long>()
        val cursor = db.rawQuery(QUERY_GET_TIMESTARTALARM_TIMEULTILEND, null)
        if (cursor.moveToFirst()) {
            ret.add(cursor.getLong(cursor.getColumnIndex(timeStartAlarm)))
            ret.add(cursor.getLong(cursor.getColumnIndex(timeUntilEnd)))
        }
        cursor.close()
        db.close()
        return ret
    }

    companion object {
        //DB GENERAL INFO
        const val DB_NAME = "StopwatchAndTimerDb"
        const val DB_VERSION = 1
        const val ID_RECORD = 1

        //TIMER STATE TABLE
        const val timerStateTable = "TIMERSTATE"
        const val idState = "id"
        const val timeStartAlarm = "time_start_alarm"
        const val timeUntilEnd = "time_until_end"
    }
}