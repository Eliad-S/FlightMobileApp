
package com.example.flightmobileapp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class JoystickActivity : AppCompatActivity(),  JoyStickView.JoystickListener {
    // The Tcp Client for communication.
    //private val tcpClient: TcpClient? = null

    // The function that runs on creation.
     override fun onCreate(savedInstanceState: Bundle?) {
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
    }

    // The function called upon destruction of the activity.
    protected override fun onDestroy() {
        super.onDestroy()
        // Stop the connection to the server.
        //tcpClient.Stop()
    }
}