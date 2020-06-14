package com.example.flightmobileapp

import Api
import android.content.Intent
import android.os.Bundle
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

class SlidersActivity : AppCompatActivity() {
    private var realThrottle: Double = 0.toDouble()
    private var realRudder: Double = 0.toDouble()
    private var lastSendThrottle = 0.toDouble()
    private var lastSendRudder = 0.toDouble()
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
                realRudder = (i.toDouble() - 100) / 100;
                textView5.text = "$realRudder"
                if ((realRudder - lastSendRudder) >= 0.02) {
                    // update server
                    setRudder(realRudder)
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
                realThrottle = i.toDouble() / 100
                textView4.text = "$realThrottle"
                if ((realThrottle - lastSendThrottle) >= 0.01) {
                    // update server
                    setThrottle(realThrottle)
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

    fun setThrottle(newVal: Double) {
        lastSendThrottle = newVal
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:SERVER_PORT/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setRudder(newVal: Double) {
        lastSendRudder = newVal
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:SERVER_PORT/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(
                    applicationContext,
                    String.format("OK"),
                    Toast.LENGTH_SHORT
                ).show()
                val allRepos: ResponseBody? = response.body()
            }
        })
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }
}