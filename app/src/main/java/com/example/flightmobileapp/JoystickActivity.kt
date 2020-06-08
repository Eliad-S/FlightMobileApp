
package com.example.flightmobileapp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class JoystickActivity : AppCompatActivity(),  JoyStickView.JoystickListener {
    // The Tcp Client for communication.
    //private val tcpClient: TcpClient? = null

    // The function that runs on creation.
    protected override fun onCreate(savedInstanceState: Bundle?) {
        // Connect to the server.
        //connectToServer().execute()
        // Init AppCompactActivity
        super.onCreate(savedInstanceState)
        // Setting the content view to the joystick's layout.
        setContentView(R.layout.activity_joystick)
    }

    // The function called when the joystick moved.
    override fun onJoystickMoved(
        xPercent: Float,
        yPercent: Float,
        source: Int
    ) {
        // Send joystick's values to the server.
//        tcpClient.sendMessage(
//            java.lang.Float.toString(xPercent),
//            java.lang.Float.toString(yPercent)
//        )
    }

    // The function called upon destruction of the activity.
    protected override fun onDestroy() {
        super.onDestroy()
        // Stop the connection to the server.
        //tcpClient.Stop()
    }

    // AsyncTask for connecting to the server
//    class connectToServer : AsyncTask<String?, String?, TcpClient?>() {
//        protected override fun doInBackground(vararg strings: String): TcpClient? {
//            tcpClient = TcpClient(
//                getIntent().getStringExtra("ip"),
//                getIntent().getStringExtra("port").toInt(),
//                object : OnMessageReceived() {
//                    fun messageReceived(message: String?) {
//                        publishProgress(message)
//                    }
//                })
//            tcpClient.run()
//            return null
//        }
//
//        protected override fun onProgressUpdate(vararg values: String) {
//            super.onProgressUpdate(*values)
//        }
//    }
}