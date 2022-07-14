package it.francesco.caldivezzi.counters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.ContactsContract
import android.view.Gravity
import android.view.View
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import it.francesco.caldivezzi.counters.database.AppConstants
import it.francesco.caldivezzi.counters.database.DataBaseHelper
import java.util.*
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.O)
class TimerActivity : AppCompatActivity() {

    //UI widgets
    private lateinit var pickerSeconds: NumberPicker
    private lateinit var pickerMinutes: NumberPicker
    private lateinit var pickerHours: NumberPicker
    private lateinit var buttonPlay: ImageButton
    private lateinit var buttonPause: ImageButton
    private lateinit var buttonStop: ImageButton
    private lateinit var buttonReset: ImageButton
    private lateinit var chronometerTimer: Chronometer

    //Toast to display that the user must select a different value
    private lateinit var toastTip: Toast

    //Pickers selected values to save
    private var initialHours: Int = 0
    private var initialMinutes: Int = 0
    private var initialSeconds: Int = 0

    //Logic instance state variables
    private var deltaTime: Long = 0
    private var running: Boolean = false

    //helpers for : Alarm, Notification and, the state of the Timer when the device is rebooted
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var alarmHelper: AlarmHelper
    private lateinit var dbOpenHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer_activity)

        //HELPERS INITIALISATIONS
        notificationHelper = NotificationHelper(this)
        dbOpenHelper = DataBaseHelper(this)
        alarmHelper = AlarmHelper(this)

        //UI WIDGETS INITIALISATIONS
        //PIICKERS
        pickerSeconds = findViewById(R.id.numberpicker_seconds)
        pickerMinutes = findViewById(R.id.numberpicker_minutes)
        pickerHours = findViewById(R.id.numberpicker_hours)
        //BUTTONS
        buttonPlay = findViewById(R.id.button_play_timer)
        buttonPause = findViewById(R.id.button_pause_timer)
        buttonStop = findViewById(R.id.button_stop_timer)
        buttonReset = findViewById(R.id.button_reset_timer)
        //CHRONOMETER
        chronometerTimer = findViewById(R.id.chronometer_timer)


        //PICKERS PROPERTIES
        pickerSeconds.minValue = AppConstants.MIN_SECONDS
        pickerSeconds.maxValue = AppConstants.MAX_SECONDS
        pickerSeconds.wrapSelectorWheel = true
        pickerMinutes.minValue = AppConstants.MIN_MINUTES
        pickerMinutes.maxValue = AppConstants.MAX_MINUTES
        pickerMinutes.wrapSelectorWheel = true
        pickerHours.minValue = AppConstants.MIN_HOURS
        pickerHours.maxValue = AppConstants.MAX_HOURS
        pickerHours.wrapSelectorWheel = true


        //RESTORE PREVIOUS VISUAL STATE OF UI WIDGETS
        val preferences = getPreferences(MODE_PRIVATE)
        restoreUi(
            preferences.getBoolean(AppConstants.RUNNING_TIMER, false), //LOGIC INSTANCE STATE
            preferences.getInt(AppConstants.VISIBILITY_PLAY_TIMER, View.VISIBLE),
            preferences.getInt(AppConstants.VISIBILITY_PAUSE_TIMER, View.INVISIBLE),
            preferences.getInt(AppConstants.VISIBILITY_STOP_TIMER, View.INVISIBLE),
            preferences.getInt(AppConstants.VISIBILITY_RESET_TIMER, View.INVISIBLE),
            preferences.getInt(AppConstants.VISIBILITY_HOURS_TIMER, View.VISIBLE),
            preferences.getInt(AppConstants.VISIBILITY_MINUTES_TIMER, View.VISIBLE),
            preferences.getInt(AppConstants.VISIBILITY_SECONDS_TIMER, View.VISIBLE),
            preferences.getInt(AppConstants.VISIBILITY_TIMER_TIMER, View.INVISIBLE),
        )
        initialSeconds = preferences.getInt(AppConstants.INITIAL_SECONDS, 0)
        initialMinutes = preferences.getInt(AppConstants.INITIAL_MINUTES, 0)
        initialHours = preferences.getInt(AppConstants.INITIAL_HOURS, 0)
        pickerSeconds.value = initialSeconds
        pickerMinutes.value = initialMinutes
        pickerHours.value = initialHours


        //LOGIC INSTANCE STATE
        deltaTime = preferences.getLong(AppConstants.DELTA_TIME_NOT_RUNNING_TIMER, 0)
        if (running) //When the app was closed or in background, the timer was running
        {
            //the variable difference is defined as follow : A - B where A and B are :
            // A) The last value displayed that the chronometer was "showing" (ex: 00:50)
            // B) Is defined as follow : C - D where C and D are :
            // C) The current absolute time
            // D) The last absolute time when data instance state was saved
            val difference = preferences.getLong(
                AppConstants.DELTA_TIME_RUNNING_TIMER,
                SystemClock.elapsedRealtime()
            ) -
                    (Calendar.getInstance().timeInMillis - preferences.getLong(
                        AppConstants.REAL_TIME_TIMER,
                        Calendar.getInstance().timeInMillis
                    ))

            if (difference > 0) //Means that there's still time remaining
            {
                chronometerTimer.base = SystemClock.elapsedRealtime() + difference
                chronometerTimer.start()
            } else //There's no more time remaining : Notification will be shown by AlertReceiver or RestartReceiver
            {
                chronometerTimer.base = SystemClock.elapsedRealtime()
                restoreUi()
            }
        } else if (deltaTime != 0L) //When the app was closed or in background, the timer wasn't running but it was displaying some time (ex: 00:50)
            chronometerTimer.base = SystemClock.elapsedRealtime() + deltaTime
        else //Timer wasn't running and it wasn't display anything (00:00) so, it must be reseted
            chronometerTimer.base = SystemClock.elapsedRealtime()


        //LISTENER FOR UI WIDGETS NB: NOT IN-PLATE DECLARATION
        buttonPlay.setOnClickListener(buttonPlayListener)
        buttonPause.setOnClickListener(buttonPauseListener)
        buttonStop.setOnClickListener(buttonStopListener)
        buttonReset.setOnClickListener(buttonResetListener)
        chronometerTimer.setOnChronometerTickListener(chronometerTickListener)
        pickerHours.setOnValueChangedListener { picker, oldVal, newVal ->
            onValueChangeListener(
                picker,
                oldVal,
                newVal
            )
        }
        pickerMinutes.setOnValueChangedListener { picker, oldVal, newVal ->
            onValueChangeListener(
                picker,
                oldVal,
                newVal
            )
        }
        pickerSeconds.setOnValueChangedListener { picker, oldVal, newVal ->
            onValueChangeListener(
                picker,
                oldVal,
                newVal
            )
        }
    }


    private fun onValueChangeListener(picker: NumberPicker, oldVal: Int, newVal: Int) {
        when (picker.id) //Saving the value displayed on pickers
        {
            R.id.numberpicker_hours -> initialHours = pickerHours.value
            R.id.numberpicker_minutes -> initialMinutes = pickerMinutes.value
            R.id.numberpicker_seconds -> initialSeconds = pickerSeconds.value
        }
    }

    val chronometerTickListener = Chronometer.OnChronometerTickListener {
        if (it.base < SystemClock.elapsedRealtime() && running) //Timer is running and has finished, it must be stopped and it must be restored the UI
        {
            it.stop()
            deltaTime = 0
            restoreUi()
            //dbOpenHelper.deleteAlarm()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    val buttonPlayListener = View.OnClickListener {
        if (pickerSeconds.value != AppConstants.MIN_SECONDS || pickerMinutes.value != AppConstants.MIN_MINUTES || pickerHours.value != AppConstants.MIN_HOURS) //Value chosed is different to 0 0 0
        {
            var timeToSet: Long = 0 //simple variable to store the the time to display on the timer
            //UI Must change, allowing only things necessary
            restoreUi(
                running,
                View.INVISIBLE,
                View.VISIBLE,
                View.VISIBLE,
                View.VISIBLE,
                View.INVISIBLE,
                View.INVISIBLE,
                View.INVISIBLE,
                View.VISIBLE
            )

            notificationHelper.removeChannel() //If there was some notification that the user has forget to delete with the swipe in the Notifications bars, the delete it


            if (deltaTime != 0L) // If it's not the first time of the click of the button play then, the timeToShow is the deltatime
                timeToSet = deltaTime
            else // It's the "first" time that the user press the button play
                timeToSet =
                    (initialHours * AppConstants.MILLISECONDS_IN_HOURS + initialMinutes * AppConstants.MILLISECONDS_IN_MINUTES + initialSeconds * AppConstants.MILLISECONDS_IN_SECONDS).toLong()

            running = true // timer is running
            alarmHelper.setAlarm(timeToSet) //set the alarm from now plus timeToset milliseconds

            //Insert values for the alarm (reboot only)
            dbOpenHelper.deleteAlarm()
            dbOpenHelper.insertAlarm(Calendar.getInstance().timeInMillis, timeToSet)


            chronometerTimer.base =
                SystemClock.elapsedRealtime() + timeToSet //set up the chronometer correctly
            chronometerTimer.start() //start the chronometer
        } else {
            //Show a message that the user must select a different value
            toastTip = Toast.makeText(
                it.context,
                getString(R.string.tip_message),
                Toast.LENGTH_SHORT
            )
            toastTip.setGravity(Gravity.BOTTOM, 0, it.rootView.height / 7)
            toastTip.show()
        }
    }


    val buttonPauseListener = View.OnClickListener {
        alarmHelper.cancelAlarm() //Remove alarms set before
        chronometerTimer.stop() //Stop the chronometer
        deltaTime =
            chronometerTimer.base - SystemClock.elapsedRealtime() //Compute the time displayed on the chronometer
        dbOpenHelper.updateAlarm(deltaTime) //Update the values for the alarm (reboot only)
        restoreUi( //UI Must change, allowing only things necessary
            false,
            View.VISIBLE,
            View.INVISIBLE,
            View.VISIBLE,
            View.VISIBLE,
            View.INVISIBLE,
            View.INVISIBLE,
            View.INVISIBLE,
            View.VISIBLE
        )
    }


    val buttonStopListener = View.OnClickListener {
        alarmHelper.cancelAlarm() //Remove alarms set before
        //Set everything to the default value
        deltaTime = 0
        chronometerTimer.base = SystemClock.elapsedRealtime()
        dbOpenHelper.deleteAlarm() //removing values set for the alarm (reboot only)
        restoreUi()
    }


    val buttonResetListener = View.OnClickListener {
        deltaTime = 0 //Time displayed is zero
        //reset the chronometer to the value choosed before by the user
        chronometerTimer.base =
            SystemClock.elapsedRealtime() + initialHours * AppConstants.MILLISECONDS_IN_HOURS + initialMinutes * AppConstants.MILLISECONDS_IN_MINUTES + initialSeconds * AppConstants.MILLISECONDS_IN_SECONDS
        dbOpenHelper.deleteAlarm()
        dbOpenHelper.insertAlarm(
            Calendar.getInstance().timeInMillis,
            (initialHours * AppConstants.MILLISECONDS_IN_HOURS + initialMinutes * AppConstants.MILLISECONDS_IN_MINUTES + initialSeconds * AppConstants.MILLISECONDS_IN_SECONDS).toLong()
        )

        if (!running) //If reset was pressed after pause button was pressed the start the timer
            chronometerTimer.start()
        //UI Must change, allowing only things necessary
        restoreUi(
            true,
            View.INVISIBLE,
            View.VISIBLE,
            View.VISIBLE,
            View.VISIBLE,
            View.INVISIBLE,
            View.INVISIBLE,
            View.INVISIBLE,
            View.VISIBLE
        )
    }


    /**
     * This function restore UI and "running" variable given the parameters
     * */
    fun restoreUi(
        running: Boolean = false,
        visibilityPlay: Int = View.VISIBLE,
        visibilityPause: Int = View.INVISIBLE,
        visibilityStop: Int = View.INVISIBLE,
        visibilityReset: Int = View.INVISIBLE,
        visibilityHours: Int = View.VISIBLE,
        visibilityMinutes: Int = View.VISIBLE,
        visibilitySeconds: Int = View.VISIBLE,
        visibilityTimer: Int = View.INVISIBLE
    ) {
        this.running = running
        buttonPlay.visibility = visibilityPlay
        buttonPause.visibility = visibilityPause
        buttonStop.visibility = visibilityStop
        buttonReset.visibility = visibilityReset
        pickerHours.visibility = visibilityHours
        pickerSeconds.visibility = visibilitySeconds
        pickerMinutes.visibility = visibilityMinutes
        chronometerTimer.visibility = visibilityTimer

    }


    override fun onPause() {
        val preferences = getPreferences(MODE_PRIVATE)
        val editor = preferences.edit()
        //Saving Visibility State
        editor.putInt(AppConstants.VISIBILITY_PLAY_TIMER, buttonPlay.visibility)
        editor.putInt(AppConstants.VISIBILITY_PAUSE_TIMER, buttonPause.visibility)
        editor.putInt(AppConstants.VISIBILITY_STOP_TIMER, buttonStop.visibility)
        editor.putInt(AppConstants.VISIBILITY_RESET_TIMER, buttonReset.visibility)
        editor.putInt(AppConstants.VISIBILITY_HOURS_TIMER, pickerHours.visibility)
        editor.putInt(AppConstants.VISIBILITY_MINUTES_TIMER, pickerMinutes.visibility)
        editor.putInt(AppConstants.VISIBILITY_SECONDS_TIMER, pickerSeconds.visibility)
        editor.putInt(AppConstants.VISIBILITY_TIMER_TIMER, chronometerTimer.visibility)
        //Saving last values selected by the user
        editor.putInt(AppConstants.INITIAL_HOURS, initialHours)
        editor.putInt(AppConstants.INITIAL_MINUTES, initialMinutes)
        editor.putInt(AppConstants.INITIAL_SECONDS, initialSeconds)
        //Saving logic variables
        editor.putBoolean(AppConstants.RUNNING_TIMER, running)
        editor.putLong(AppConstants.DELTA_TIME_NOT_RUNNING_TIMER, deltaTime)
        editor.putLong(
            AppConstants.DELTA_TIME_RUNNING_TIMER,
            chronometerTimer.base - SystemClock.elapsedRealtime()
        )
        editor.putLong(AppConstants.REAL_TIME_TIMER, Calendar.getInstance().timeInMillis)
        editor.apply()
        super.onPause()
    }
}