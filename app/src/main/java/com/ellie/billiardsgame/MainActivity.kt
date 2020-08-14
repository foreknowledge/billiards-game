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
    private val ballRadius by lazy {
        resources.getDimension(R.dimen.ball_size) / 2
    }

    private lateinit var ballGestureDetector : GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithNoStatusBar()

        setTouchEvents()
    }

    private fun setContentViewWithNoStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
    }

    private fun setTouchEvents() {
        initBallGestureDetector()
        setBallTouchEvent()

    }

    private fun initBallGestureDetector() {
        ballGestureDetector = GestureDetectorCompat(this, object: GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                e2?.let { event ->
                    whiteBall.x = event.rawX - ballRadius
                    whiteBall.y = event.rawY - ballRadius
                }

                return true
            }
        })
    }

    private fun setBallTouchEvent() {
        whiteBall.setOnTouchListener { v, event ->
            ballGestureDetector.onTouchEvent(event)
            true
        }
    }
}