package com.example.flightmobileapp

import Api
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class SlidersActivity : AppCompatActivity(){
    // sliders
    private var lastSendThrottle = 0.toDouble()
    private var lastSendRudder = 0.toDouble()
    // joystick
    private var lastSendAileron = 0.toDouble()
    private var lastSendElevator = 0.toDouble()

    private var url : String? = null
    private var changeImage = false
    private lateinit var builder : AlertDialog.Builder
    var alertDialog : AlertDialog? = null
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
        builder = AlertDialog.Builder(this)
        setButtonsMessage()
        alertDialog = builder.create()
        onStart()
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
                Toast.makeText(applicationContext, t.message,
                    Toast.LENGTH_SHORT).show()
                return
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!changeImage) {
                    return
                }
                if (response.code() != 200) {
                    val message = getResponseMessage(response)
                    showMessage(message)
                }
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

    fun showMessage(message : String) {
        val dialogMessage: String = alertDialog?.findViewById<TextView>(android.R.id.message)?.text.toString()
        val newMessage = message + "\nDo you want to return to the login screen?"
        if (alertDialog?.isShowing == true && (dialogMessage == newMessage)) {
            return
        }
        alertDialog = builder.setMessage(message + "\nDo you want to return to the login screen?").show()
    }

    fun setButtonsMessage() {
        builder.setTitle("Androidly Alert")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            onStop()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton(android.R.string.no) { dialog, which ->

        }
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
                if (!changeImage) {
                    return
                }
                Toast.makeText(applicationContext, t.message,
                    Toast.LENGTH_SHORT).show()
                return
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (!changeImage) {
                    return
                }
                val inputstream = response?.body()?.byteStream()
                if (inputstream == null) {
                    val message = getResponseMessage(response)
                    showMessage(message)
                }
                val bitmap = BitmapFactory.decodeStream(inputstream)
                runOnUiThread {
                    val imageView = findViewById<ImageView>(R.id.imageView)
                    imageView.setImageBitmap(bitmap)
                    println("debug:got image succsesfuly")
                }
            }
        })

    }

    companion object {
        public fun getResponseMessage(response: Response<ResponseBody>): String {
            var reader: BufferedReader? = null
            val sb = StringBuilder()
            try {
                reader =
                    BufferedReader(InputStreamReader(response.errorBody()!!.byteStream()))
                var line: String?
                try {
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val finallyError = sb.toString()
            return finallyError
        }
    }
}