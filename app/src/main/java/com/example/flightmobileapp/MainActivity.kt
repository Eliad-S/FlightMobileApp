package com.example.flightmobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

//import androidx.room.RoomDatabase

class MainActivity : AppCompatActivity() {
    //private var rdb : RoomDatabase? = null
    private lateinit var server1 : TextView
    private lateinit var server2 : TextView
    private lateinit var server3 : TextView
    private lateinit var server4 : TextView
    private lateinit var server5 : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        server1 = findViewById(R.id.server1) as TextView
        server2 = findViewById(R.id.server2) as TextView
        server3 = findViewById(R.id.server3) as TextView
        server4 = findViewById(R.id.server4) as TextView
        server5 = findViewById(R.id.server5) as TextView

        findViewById<Button>(R.id.connect_button).setOnClickListener {
            connectButtonOnClick(it)
        }
    }

    fun connectButtonOnClick(view: View) {
        val url = findViewById(R.id.url_edit_text) as EditText
        server1.setText(url.text)


        val intent = Intent(baseContext, JoystickActivity::class.java)
//        intent.putExtra("ip", ipFromUser.getText().toString())
//        intent.putExtra("port", portFromUser.getText().toString())
        startActivity(intent)

    }
}