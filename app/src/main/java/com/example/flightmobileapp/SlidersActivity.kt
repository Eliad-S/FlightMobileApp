package com.example.flightmobileapp

import Api
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_sliders.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SlidersActivity : AppCompatActivity(){
    // sliders
    private var realThrottle: Double = 0.toDouble()
    private var realRudder: Double = 0.toDouble()
    private var lastSendThrottle = 0.toDouble()
    private var lastSendRudder = 0.toDouble()

    // joystick
    private var realAileron: Double = 0.toDouble()
    private var realElevator: Double = 0.toDouble()
    private var lastSendAileron = 0.toDouble()
    private var lastSendElevator = 0.toDouble()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliders)
        // back button to the main activity
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // set a seekbar to the rudder
        seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                val rudder = (i.toDouble() - 100) / 100;
                textView5.text = "$rudder"
                if ((realRudder - lastSendRudder) >= 0.02) {
                    // update server
                    realRudder = rudder
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })
        // set a seekbar to the throttle
        seekBar4.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                val throttle = i.toDouble() / 100
                throttleText.text = "$throttle"
                if ((realThrottle - lastSendThrottle) >= 0.01) {
                    // update server
                    realThrottle = throttle
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })
        setJoystick()
    }

    fun setCommand() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:44310/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }
        })
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }

    private fun setJoystick() {
        joystickView.setOnMoveListener { angle, strength ->
            val aileron = kotlin.math.cos(Math.toRadians(angle.toDouble())) * strength / 100
            val elevator = kotlin.math.sin(Math.toRadians(angle.toDouble())) * strength / 100
            aileronText.setText(aileron.toDouble().toString())
            elevatorText.setText(elevator.toDouble().toString())
            var difference = kotlin.math.abs(aileron - lastSendAileron)
            if (difference / 2 > 0.01) {
                // set aileron
                realAileron = aileron
                setCommand()
            }
            difference = kotlin.math.abs(elevator - lastSendElevator)
            if (difference / 2 > 0.01) {
                // set elevator
                realElevator = elevator
                setCommand()
            }

        }
    }
}