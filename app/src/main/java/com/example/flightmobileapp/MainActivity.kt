package com.example.flightmobileapp
import Api
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


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
        onStart()
    }

    fun connectButtonOnClick(view: View) {
        // the user url
        val editTextString = findViewById<EditText>(R.id.url_edit_text).text.toString()
        // the user did not insert an url
        if (TextUtils.isEmpty(editTextString)) {
            Toast.makeText(this, "You did not enter a URL", Toast.LENGTH_SHORT).show()
        } else {
            val url = Url(editTextString)
            urlViewModel.insert(url)
            urlViewModel.updateNumbers()
            try {
                val gson = GsonBuilder().setLenient().create()
                val retrofit = Retrofit.Builder().baseUrl(editTextString)
                    .addConverterFactory(GsonConverterFactory.create(gson)).build()
                val api = retrofit.create(Api::class.java)
                api.getImg().enqueue(object : Callback<ResponseBody> {
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            "Can't Connect, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val inputstream = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputstream)
                        nextActivity(editTextString)
                    }
                })
            } catch (e : Exception) {
                Toast.makeText(
                    applicationContext,
                    "Can't Connect, try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

//            val urlconnect : URL
//            try {
//                // try connect
//                urlconnect = URL(editTextString)
//                val connect : HttpURLConnection = urlconnect.openConnection() as HttpURLConnection
//
//                // create the second screen
//                val intent = Intent(this, SlidersActivity::class.java)
//                intent.putExtra("url", editTextString)
//                startActivity(intent)
//            } catch (e: Exception) {
//                Toast.makeText(this, "Can't Connect, try again", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun nextActivity(url : String) {
        // create the second screen
        val intent = Intent(this, SlidersActivity::class.java)
        intent.putExtra("url", url)
        startActivity(intent)
    }

    fun updateEditText(url: Url) {
        val editTextString = findViewById<EditText>(R.id.url_edit_text)
        editTextString.setText(url.url)
        urlViewModel.insert(Url(url.url))
        urlViewModel.updateNumbers()
    }
}