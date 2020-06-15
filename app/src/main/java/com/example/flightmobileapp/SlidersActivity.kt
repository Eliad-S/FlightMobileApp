package com.example.flightmobileapp

import Api
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_sliders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

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
    private var url : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliders)
        url = getIntent().getStringExtra("url")
        findViewById<Button>(R.id.button).setText(url)
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
                val rudder = (i.toDouble() - 100) / 100
                textView5.text = "$rudder"
                if ((realRudder - lastSendRudder) >= 0.02) {
                    // update server
                    realRudder = rudder
                    CoroutineScope(IO).launch { setCommand() }
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
                    CoroutineScope(IO).launch { setCommand() }
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

        CoroutineScope(IO).launch { imageRequest() }

    }

    private suspend fun imageRequest() {
        while(true) {
            getSimulatorScreen()
            delay(1000)
        }
    }

    fun setCommand() {
        lastSendRudder = realRudder
        lastSendThrottle = realThrottle
        lastSendAileron = realAileron
        lastSendElevator = realElevator
        val json: String = "{\n" + " \"aileron\": " + realAileron + ",\n" + " \"rudder\": " + realRudder + ",\n" + " \"elevator\": " + realElevator + ",\n" + " \"throttle\": " + realThrottle + "\n}"
        val rb: RequestBody = RequestBody.create(MediaType.parse("application/json"), json)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.post(rb).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    Log.d("FlightMobileApp", response.body().toString())
                }
                catch (e: IOException) {e.printStackTrace()}
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
            var isChange = false
            if (difference / 2 > 0.01) {
                // set aileron
                realAileron = aileron
                isChange = true
            }
            difference = kotlin.math.abs(elevator - lastSendElevator)
            if (difference / 2 > 0.01) {
                // set elevator
                realElevator = elevator
                isChange = true
            }
            if (isChange) {
                CoroutineScope(IO).launch { setCommand() }
            }
        }
    }


    private suspend fun  getSimulatorScreen() {
        var flag = 0
        var bitmapStream: Bitmap? = null
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(this.url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        val api = retrofit.create(Api::class.java)

        api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, "Connection failed",
                    Toast.LENGTH_SHORT).show()
                return
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val imgStream = response.body()?.byteStream()
                 bitmapStream = BitmapFactory.decodeStream(imgStream)
                flag = 1
//                withContext(Main) {  imageView.setImageBitmap(bitmapStream) }
//                runOnUiThread {
//                    imageView.setImageBitmap(bitmapStream)
//                }
            }
        })

        if (flag == 1) {
            withContext(Main) {  imageView.setImageBitmap(bitmapStream) }
        }

    }
}