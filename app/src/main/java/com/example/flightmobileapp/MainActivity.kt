package com.example.flightmobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.LruCache
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.RoomDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var server1 : TextView
    private lateinit var server2 : TextView
    private lateinit var server3 : TextView
    private lateinit var server4 : TextView
    private lateinit var server5 : TextView
    private var rdb : RoomDatabase? = null
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
        val server = findViewById(R.id.server1) as TextView
        server1.setText(url.text);
        ////va r size = cache.size();
        //cache.put(url.toString(), 1000);
        //var list = cache.snapshot();
        //var i = size;
//        for(item in list) {
//            if(i == 1) {
//                view.findViewById<TextView>(R.id.server1).text = "edrf"
//            }
//            if(i == 2) {
//                view.findViewById<TextView>(R.id.server2).text = item.key.toString()
//            }
//            if(i == 3) {
//                view.findViewById<TextView>(R.id.server3).text = item.key.toString()
//            }
//            if(i == 4) {
//                view.findViewById<TextView>(R.id.server4).text = item.key.toString()
//            }
//            if(i == 5) {
//                view.findViewById<TextView>(R.id.server5).text = item.key.toString()
//            }
//            i--;
//        }
    }
}