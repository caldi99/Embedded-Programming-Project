package it.francesco.caldivezzi.counters.database

class AppConstants {
    companion object{

        //NOTIFICATION HELPER
        const val CHANNEL_ID = "notificationChannelId"
        const val CHANNEL_NAME = "notificationChannel"

        //ALERT RECEIVER
        const val ID_NOTIFICATION = 1

        //ALARM HELPER
        const val REQUEST_CODE :Int = 1
        const val FLAG : Int = 0

        //STOPWATCH ACTIVITY
        //Visibility For Stopwatch Activity
        const val VISIBILITY_PLAY_STOPWATCH ="play_visibility_stopwatch"
        const val VISIBILITY_PAUSE_STOPWATCH ="pause_visibility_stopwatch"
        const val VISIBILITY_STOP_STOPWATCH ="stop_visibility_stopwatch"
        const val VISIBILITY_RESET_STOPWATCH ="reset_visibility_stopwatch"
        //Logic Constants For Stopwatch Activity
        const val RUNNING_STOPWATCH ="running_stopwatch"
        const val DELTA_TIME_RUNNING_STOPWATCH ="delta_time_running_stopwatch"
        const val DELTA_TIME_NOT_RUNNING_STOPWATCH = "delta_time_not_running_stopwatch"
        const val REAL_TIME_STOPWATCH = "real_time_stopwatch"


        //TIMER ACTIVITY
        //Conversion Value
        const val MILLISECONDS_IN_HOURS = 3600000
        const val MILLISECONDS_IN_MINUTES = 60000
        const val MILLISECONDS_IN_SECONDS = 1000
        //Setting for pickers
        const val MIN_SECONDS = 0
        const val MIN_MINUTES = 0
        const val MIN_HOURS = 0
        const val MAX_SECONDS = 59
        const val MAX_MINUTES = 59
        const val MAX_HOURS = 23
        //Visibility Constants For Timer Activity
        const val VISIBILITY_PLAY_TIMER = "play_visibility_timer"
        const val VISIBILITY_PAUSE_TIMER = "pause_visibility_timer"
        const val VISIBILITY_STOP_TIMER = "stop_visibility_timer"
        const val VISIBILITY_RESET_TIMER = "reset_visibility_timer"
        const val VISIBILITY_HOURS_TIMER ="hours_visibility_timer"
        const val VISIBILITY_MINUTES_TIMER ="minutes_visibility_timer"
        const val VISIBILITY_SECONDS_TIMER ="seconds_visibility_timer"
        const val VISIBILITY_TIMER_TIMER = "timer_visibility_timer"
        //Last Value displayed on Pickers For Timer Activity
        const val INITIAL_HOURS = "initial_hours_timer"
        const val INITIAL_MINUTES = "initial_minutes_timer"
        const val INITIAL_SECONDS = "initial_seconds_timer"
        //Logic Constants For Timer Activity
        const val RUNNING_TIMER = "running_timer"
        const val DELTA_TIME_RUNNING_TIMER = "delta_time_running_timer"
        const val DELTA_TIME_NOT_RUNNING_TIMER = "delta_time_not_running_timer"
        const val REAL_TIME_TIMER = "real_time_timer"

        const val ACTION_DELETE_TIMER = "DELETE"
    }
}