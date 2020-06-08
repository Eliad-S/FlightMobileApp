package com.example.flightmobileapp

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener


// The Joystick Surface view.
class JoyStickView : SurfaceView, SurfaceHolder.Callback, OnTouchListener {
    // Middle X value.
    private var centerOfX = 0f

    // Middle Y value.
    private var centerOfY = 0f

    // The radius of the circle.
    private var raduisCircle = 0f

    // The radius of the hat.
    private var hatRadius = 0f

    // The Joystick listener.
    private var JoystickCollable: JoystickListener? = null

    // The ratio
    private val rat = 5

    // Joystick listener interface definition.
    interface JoystickListener {
        fun onJoystickMoved(
            xPercent: Float,
            yPercent: Float,
            source: Int
        )
    }

    // // Initialization of dimensional members.
    private fun setupDim() {
        centerOfX = width / 2.toFloat()
        centerOfY = height / 2.toFloat()
        raduisCircle = Math.min(width, height) / 3.toFloat()
        hatRadius = Math.min(width, height) / 5.toFloat()
    }

    // Constructor.
    constructor(context: Context?) : super(context) {
        setOnTouchListener(this)
        if (context is JoystickListener) {
            JoystickCollable = context
        }
    }

    // Constructor
    constructor(
        context: Context?,
        attributes: AttributeSet?,
        style: Int
    ) : super(context, attributes, style) {
        setOnTouchListener(this)
        if (context is JoystickListener) {
            JoystickCollable = context
        }
    }

    // Constructor.
    constructor(context: Context?, attributes: AttributeSet?) : super(
        context,
        attributes
    ) {
        holder.addCallback(this)
        setOnTouchListener(this)
        if (context is JoystickListener) {
            JoystickCollable = context
        }
    }

    // The function that draws the joystick.
    private fun DrawJoystick(newX: Float, newY: Float) {
        // Check if the surface is available
        if (holder.surface.isValid) {
            val myCanvas = this.holder.lockCanvas()
            val colors = Paint()
            myCanvas.drawRGB(250, 235, 215)
            colors.setARGB(255, 169, 169, 169)
            myCanvas.drawCircle(centerOfX, centerOfY, raduisCircle, colors)
            for (i in 0..(hatRadius / rat).toInt()) {
                colors.setARGB(255, 65, 105, 225)
                myCanvas.drawCircle(newX, newY, hatRadius - i.toFloat() * rat / 2, colors)
            }
            holder.unlockCanvasAndPost(myCanvas)
        }
    }

    // The function is called when a touch event occurs.
    override fun onTouch(v: View, e: MotionEvent): Boolean {
        if (v == this) {
            if (v == this) {
                if (e.action != MotionEvent.ACTION_UP) {
                    val displacement = Math.sqrt(
                        Math.pow(e.x - centerOfX.toDouble(), 2.0) +
                                Math.pow(e.y - centerOfY.toDouble(), 2.0)
                    ).toFloat()
                    if (displacement < raduisCircle) {
                        DrawJoystick(e.x, e.y)
                        JoystickCollable!!.onJoystickMoved(
                            (e.x - centerOfX) / raduisCircle
                            , (e.y - centerOfY) / raduisCircle, id
                        )
                    } else {
                        val ratio = raduisCircle / displacement
                        val constraiedX = centerOfX + (e.x - centerOfX) * ratio
                        val constraiedY = centerOfY + (e.y - centerOfY) * ratio
                        DrawJoystick(constraiedX, constraiedY)
                        JoystickCollable!!.onJoystickMoved(
                            (constraiedX - centerOfX) / raduisCircle
                            , (constraiedY - centerOfY) / raduisCircle, id
                        )
                    }
                } else {
                    DrawJoystick(centerOfX, centerOfY)
                    JoystickCollable!!.onJoystickMoved(0f, 0f, id)
                }
            }
        } else {
            DrawJoystick(centerOfX, centerOfY)
        }
        return true
    }

    // This function is called when the surface is created.
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        setupDim()
        DrawJoystick(centerOfX, centerOfY)
    }

    // This function is called when the surface is modified.
    override fun surfaceChanged(
        surfaceHolder: SurfaceHolder,
        format: Int,
        width: Int,
        high: Int
    ) {
    }

    // This function is called when the surface is destroyed.
    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {}
}