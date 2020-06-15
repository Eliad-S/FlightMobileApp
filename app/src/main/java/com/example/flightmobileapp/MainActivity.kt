package com.example.flightmobileapp
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.internal.bind.TypeAdapters.URL
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
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
            val urlconnect : URL
            try {
                urlconnect = URL(editTextString)
                var connect = urlconnect.openConnection() as HttpURLConnection

                val intent = Intent(this, SlidersActivity::class.java)
                intent.putExtra("url", editTextString)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Can't Connect, try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateEditText(url: Url) {
        val editTextString = findViewById<EditText>(R.id.url_edit_text)
        editTextString.setText(url.url)
        urlViewModel.insert(Url(url.url))
        urlViewModel.updateNumbers()
    }
}