package com.ellie.billiardsgame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
    private val gestureDetector by lazy {
        GestureDetectorCompat(this, object: GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                e2?.let { event ->
                    whiteBall.x = event.rawX - whiteBall.width / 2
                    whiteBall.y = event.rawY - whiteBall.width / 2
                }

                return true
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNoStatusBar()
        setContentView(R.layout.activity_main)

        whiteBall.setOnTouchListener { v, event ->
           gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setNoStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}