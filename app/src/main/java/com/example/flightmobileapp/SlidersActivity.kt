package com.example.flightmobileapp

import Api
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_sliders.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    private var lastSendThrottle = 0.toDouble()
    private var lastSendRudder = 0.toDouble()
    // joystick
    private var lastSendAileron = 0.toDouble()
    private var lastSendElevator = 0.toDouble()

    private var url : String? = null
    private var changeImage = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliders)
        url = getIntent().getStringExtra("url")
        // back button to the main activity
        val backButton = findViewById<Button>(R.id.buttonBack)
        backButton.setOnClickListener{
            onStop()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val seekbar = findViewById<SeekBar>(R.id.seekBarRudder)
        seekbar.setProgress(seekbar.max / 2)
        // set a seek bar to the rudder
        findViewById<SeekBar>(R.id.seekBarRudder).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                val rudderValue = (i.toDouble() - 100) / 100
                rudder.text = "$rudderValue"
                if (kotlin.math.abs(rudderValue - lastSendRudder) >= 0.02) {
                    // update server
                    lastSendRudder = rudderValue
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
        // set a seek bar to the throttle
        findViewById<SeekBar>(R.id.seekBarThrottle).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                val throttleValue = i.toDouble() / 100
                throttle.text = "$throttleValue"
                if (kotlin.math.abs(throttleValue - lastSendThrottle) >= 0.01) {
                    // update server
                    lastSendThrottle = throttleValue
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
        setCommand()
        //onStart()
        onStart();
    }

    private fun imageRequest() {
        changeImage = true
        CoroutineScope(IO).launch {
            while (changeImage) {
                getSimulatorScreen()
                delay(250)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!changeImage) {
            imageRequest()
        }
    }

    override fun onResume() {
        super.onResume()
        if(!changeImage) {
            imageRequest()
        }
    }

    override fun onPause() {
        super.onPause()
        if (changeImage) {
            changeImage = false
        }
    }

    override fun onStop() {
        super.onStop()
        changeImage = false
    }



    fun setCommand() {
        val json: String =
            "{\"aileron\": $lastSendAileron,\n \"rudder\": $lastSendRudder,\n \"elevator\": $lastSendElevator,\n \"throttle\": $lastSendThrottle\n}"
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
                Toast.makeText(applicationContext, t.message,
                    Toast.LENGTH_SHORT).show()
                return
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    Log.d("FlightMobileApp", response.body().toString())
                    println("make the update correctly")

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    println("failed to make any post: catch")
                }
            }
        })
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }

    private fun setJoystick() {
        joystickView.setOnMoveListener { angle, strength ->
            val aileronValue = kotlin.math.cos(Math.toRadians(angle.toDouble())) * strength / 100
            val elevatorValue = kotlin.math.sin(Math.toRadians(angle.toDouble())) * strength / 100
            aileron.setText(aileronValue.toString())
            elevator.setText(elevatorValue.toString())
            var difference = kotlin.math.abs(aileronValue - lastSendAileron)
            var isChange = false
            if (difference / 2 > 0.01) {
                // set aileron
                lastSendAileron = aileronValue
                isChange = true
            }
            difference = kotlin.math.abs(elevatorValue - lastSendElevator)
            if (difference / 2 > 0.01) {
                // set elevator
                lastSendElevator = elevatorValue
                isChange = true
            }
            if (isChange) {
                CoroutineScope(IO).launch { setCommand() }
            }
        }
    }


    private  fun  getSimulatorScreen() {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(this.url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        val api = retrofit.create(Api::class.java)

        api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(applicationContext, t.message,
                    Toast.LENGTH_SHORT).show()
                return
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val inputstream = response?.body()?.byteStream()
                println(response.message());
                println(inputstream.toString());
                println(response.errorBody().toString());
                println(response.isSuccessful);
                println(response.headers());

                val bitmap = BitmapFactory.decodeStream(inputstream)
                runOnUiThread {
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    imageView.setImageBitmap(bitmap)
                    println("debug:got image succsesfuly")
                }
            }
        })

    }
}