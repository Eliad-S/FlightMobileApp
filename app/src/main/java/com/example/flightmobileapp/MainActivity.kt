package com.example.flightmobileapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//import androidx.room.RoomDatabase

class MainActivity : AppCompatActivity() {
    //private var rdb : RoomDatabase? = null
    private lateinit var server1: TextView
    private lateinit var server2: TextView
    private lateinit var server3: TextView
    private lateinit var server4: TextView
    private lateinit var server5: TextView

    private lateinit var urlViewModel: UrlViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = UrlListAdapter(this,{url: Url -> updateEditText(url)})
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        urlViewModel = ViewModelProvider(this).get(UrlViewModel::class.java)
        urlViewModel.allUrls.observe(this, Observer { urls ->
            // Update the cached copy of the words in the adapter.
            urls?.let { adapter.setUrls(it) }
        })


//        server1 = findViewById(R.id.server1) as TextView
//        server2 = findViewById(R.id.server2) as TextView
//        server3 = findViewById(R.id.server3) as TextView
//        server4 = findViewById(R.id.server4) as TextView
//        server5 = findViewById(R.id.server5) as TextView

        findViewById<Button>(R.id.connect_button).setOnClickListener {
            connectButtonOnClick(it)
        }


    }



    fun connectButtonOnClick(view: View) {
        val editTextString = findViewById<EditText>(R.id.url_edit_text).text.toString()
        if (TextUtils.isEmpty(editTextString)) {
            Toast.makeText(this, "You did not enter a URL", Toast.LENGTH_SHORT).show()
        } else {
            val url = Url(editTextString)
            urlViewModel.insert(url)
            urlViewModel.updateNumbers()
            //server1.setText(url.text)
        }

        val intent = Intent(baseContext, SlidersActivity::class.java)
        startActivity(intent)

    }

    fun updateEditText(url: Url) {
        var editTextString = findViewById<EditText>(R.id.url_edit_text)
        editTextString.setText(url.url)
        urlViewModel.insert(Url(url.url))
        urlViewModel.updateNumbers()
    }
}