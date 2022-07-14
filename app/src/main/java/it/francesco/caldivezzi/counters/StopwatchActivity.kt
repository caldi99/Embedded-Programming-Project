package it.francesco.caldivezzi.counters

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Chronometer
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import it.francesco.caldivezzi.counters.database.AppConstants
import java.util.*

class StopwatchActivity : AppCompatActivity()
{
    //UI Variables
    private lateinit var buttonPlay : ImageButton
    private lateinit var buttonPause : ImageButton
    private lateinit var buttonStop : ImageButton
    private lateinit var buttonReset : ImageButton
    private lateinit var chronometerStopwatch: Chronometer

    //Logic Variables
    private var deltaTime : Long = 0
    private var running : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stopwatch_activity)

        //UI variables Initialization
        buttonPlay = findViewById(R.id.button_play_stopwatch)
        buttonPause = findViewById(R.id.button_pause_stopwatch)
        buttonStop = findViewById(R.id.button_stop_stopwatch)
        buttonReset = findViewById(R.id.button_reset_stopwatch)
        chronometerStopwatch = findViewById(R.id.chronometer_stopwatch)

        //Restore previous state
        val preferences = getPreferences(MODE_PRIVATE)
        running = preferences.getBoolean(AppConstants.RUNNING_STOPWATCH,false)
        buttonPlay.visibility = preferences.getInt(AppConstants.VISIBILITY_PLAY_STOPWATCH, View.VISIBLE)
        buttonPause.visibility = preferences.getInt(AppConstants.VISIBILITY_PAUSE_STOPWATCH, View.INVISIBLE)
        buttonStop.visibility = preferences.getInt(AppConstants.VISIBILITY_STOP_STOPWATCH, View.INVISIBLE)
        buttonReset.visibility = preferences.getInt(AppConstants.VISIBILITY_RESET_STOPWATCH, View.INVISIBLE)
        deltaTime = preferences.getLong(AppConstants.DELTA_TIME_NOT_RUNNING_STOPWATCH,0)


        if(running) //If the stopwatch was running then compute the time to assign to the stopwatch which is defined as follow : A - B - C which are:
        {           // A) Is the current time passed since the boot of the device
                    // B) Is the defined as follow : D - E which are :
                        // D) Current absolute time
                        // E) The last absolute time when data instance state was saved
                    // C) The last value displayed on the stopwatch ex: 00:50
            chronometerStopwatch.base = SystemClock.elapsedRealtime() -
                    (Calendar.getInstance().timeInMillis - preferences.getLong(AppConstants.REAL_TIME_STOPWATCH, Calendar.getInstance().timeInMillis)) -
                    (preferences.getLong(AppConstants.DELTA_TIME_RUNNING_STOPWATCH,0))
            chronometerStopwatch.start()
        }else if(deltaTime != 0L) //If it was not running but it was displayed something restore the value
            chronometerStopwatch.base = SystemClock.elapsedRealtime() - deltaTime
        else //If it was not running and it wasn't display anything the restart the timer
            chronometerStopwatch.base = SystemClock.elapsedRealtime()

        //Set up listener
        buttonPlay.setOnClickListener(buttonPlayListener)
        buttonPause.setOnClickListener(buttonPauseListener)
        buttonStop.setOnClickListener(buttonStopListener)
        buttonReset.setOnClickListener(buttonResetListener)
    }

    val buttonPlayListener = View.OnClickListener {

        if(!running)// If the timer wasn't running then restore the timer and change UI
        {
            restoreUi(true,View.INVISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE)
            chronometerStopwatch.base = SystemClock.elapsedRealtime() - deltaTime
            chronometerStopwatch.start()
        }
    }


    val buttonPauseListener = View.OnClickListener {
        //stop the stopwatch, change UI and "save" time displayed
        restoreUi(false,View.VISIBLE,View.INVISIBLE,View.VISIBLE,View.VISIBLE)
        deltaTime = SystemClock.elapsedRealtime() - chronometerStopwatch.base
        chronometerStopwatch.stop()
    }


    val buttonStopListener = View.OnClickListener {
        //reset UI, and stopwatch
        restoreUi()
        chronometerStopwatch.stop()
        chronometerStopwatch.base = SystemClock.elapsedRealtime()
        deltaTime = 0
    }

    val buttonResetListener = View.OnClickListener {
        //Start Counting again from 00:00
        chronometerStopwatch.base = SystemClock.elapsedRealtime()
        deltaTime = 0
        if(!running)
        {
            restoreUi(true,View.INVISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE)
            chronometerStopwatch.start()
        }
    }


    //HELPER FUNCTION
    /**
     * This function restore UI and "running" variable given the parameters
     * */
    fun restoreUi(
        running: Boolean = false,
        visibilityPlay: Int = View.VISIBLE,
        visibilityPause: Int = View.INVISIBLE,
        visibilityStop: Int = View.INVISIBLE,
        visibilityReset: Int = View.INVISIBLE,
    )
    {
        this.running = running
        buttonPlay.visibility = visibilityPlay
        buttonPause.visibility = visibilityPause
        buttonStop.visibility = visibilityStop
        buttonReset.visibility = visibilityReset
    }



    override fun onPause()
    {
        val preferences = getPreferences(MODE_PRIVATE)
        val editor = preferences.edit()

        //SAVE UI STATE
        editor.putInt(AppConstants.VISIBILITY_PLAY_STOPWATCH, buttonPlay.visibility)
        editor.putInt(AppConstants.VISIBILITY_PAUSE_STOPWATCH, buttonPause.visibility)
        editor.putInt(AppConstants.VISIBILITY_STOP_STOPWATCH, buttonStop.visibility)
        editor.putInt(AppConstants.VISIBILITY_RESET_STOPWATCH, buttonReset.visibility)

        //SAVE LOGIC STATE
        editor.putBoolean(AppConstants.RUNNING_STOPWATCH,running)
        editor.putLong(AppConstants.DELTA_TIME_NOT_RUNNING_STOPWATCH, deltaTime)
        editor.putLong(AppConstants.DELTA_TIME_RUNNING_STOPWATCH, SystemClock.elapsedRealtime() - chronometerStopwatch.base)
        editor.putLong(AppConstants.REAL_TIME_STOPWATCH,Calendar.getInstance().timeInMillis)
        editor.apply()
        super.onPause()
    }

}