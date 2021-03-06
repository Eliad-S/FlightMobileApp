package com.example.flightmobileapp

import Api
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.flightmobileapp.MainActivity.Companion.bitmap
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_game.*
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
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import kotlin.random.Random.Default.Companion


class GameActivity : AppCompatActivity() {
    private var url : String? = null
    private var changeImage = false
    // sliders
    private var lastSendThrottle = 0.toDouble()
    private var lastSendRudder = 0.toDouble()
    // joystick
    private var lastSendAileron = 0.toDouble()
    private var lastSendElevator = 0.toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        url = intent.getStringExtra("url")
        //set image.
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageBitmap(bitmap)

        // set a seek bar to the rudder
        setRudderSlider()
        // set a seek bar to the throttle
        setThrottleSlider()
        setJoystick()
        // initialize the values
        setCommand()
        onStart()
    }

    private fun setRudderSlider() {
        val seekbar = findViewById<SeekBar>(R.id.seekBarRudder)
        seekbar.progress = seekbar.max / 2
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                val rudderValue = (i.toDouble() - 100) / 100
                findViewById<TextView>(R.id.rudder).text = "$rudderValue"
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
    }

    private fun setThrottleSlider() {
        findViewById<SeekBar>(R.id.seekBarThrottle).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                val throttleValue = i.toDouble() / 100
                findViewById<TextView>(R.id.throttle).text = "$throttleValue"
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
                if (!changeImage) {
                    return
                }
                Toast.makeText(applicationContext, "Failed to ",
                    Toast.LENGTH_SHORT).show()
                return
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!changeImage) {
                    return
                }
                if (response.code() != 200) {
                    val message = getResponseMessage(response)
                        if (message == "Failed to update command") {
                        Toast.makeText(applicationContext, message,
                            Toast.LENGTH_SHORT).show()
                    }
                    showMessage(message)
                }
            }
        })
    }

    fun showMessage(message : String) {
        Toast.makeText(applicationContext, message + "\nyou can return the login page",
            Toast.LENGTH_SHORT).show()
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }

    private fun setJoystick() {
        joystickView.setOnMoveListener { angle, strength ->
            // get values
            val aileronValue = kotlin.math.cos(Math.toRadians(angle.toDouble())) * strength / 100
            val elevatorValue = kotlin.math.sin(Math.toRadians(angle.toDouble())) * strength / 100
            // show values
            aileron.setText(aileronValue.toString())
            elevator.setText(elevatorValue.toString())
            // check it it is an important change
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
                if (!changeImage) {
                    return
                }
                if (t.message?.contains("timeout") == true) {
                    Toast.makeText(applicationContext, "Timeout request",
                        Toast.LENGTH_SHORT).show()
                } else {
                    showMessage("Connection failed")
                }
                return
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!changeImage) {
                    return
                }
                val inputstream = response.body()?.byteStream()
                if (inputstream == null) {
                    val message = getResponseMessage(response)
                    showMessage(message)
                }
                val bitmap = BitmapFactory.decodeStream(inputstream)
                runOnUiThread {
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    imageView.setImageBitmap(bitmap)
                }
            }
        })

    }

    companion object {
        fun getResponseMessage(response: Response<ResponseBody>): String {
            var reader: BufferedReader? = null
            val sb = StringBuilder()
            try {
                reader =
                    BufferedReader(InputStreamReader(response.errorBody()!!.byteStream()))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val finallyError = sb.toString()
            return finallyError
        }
    }
}