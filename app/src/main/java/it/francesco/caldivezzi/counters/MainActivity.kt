package it.francesco.caldivezzi.counters





import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity()
{
    //UI Widgets
    private lateinit var buttonStopwatch: Button
    private lateinit var buttonTimer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialization UI Widgets
        buttonStopwatch = findViewById(R.id.button_stopwatch)
        buttonTimer = findViewById(R.id.button_timer)

        //Listener for UI Widgets
        buttonStopwatch.setOnClickListener {
            startActivity(Intent(it.context, StopwatchActivity::class.java))
        }
        buttonTimer.setOnClickListener {
            startActivity(Intent(it.context, TimerActivity::class.java))
        }
    }

}


