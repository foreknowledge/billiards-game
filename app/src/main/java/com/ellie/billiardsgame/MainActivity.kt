package com.ellie.billiardsgame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
    private val ballRadius by lazy { resources.getDimension(R.dimen.ball_size) / 2 }
    private val poolTableBorder by lazy { ViewBorder(poolTable) }

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
        setBallTouchEvent()
    }

    private fun setBallTouchEvent() {
        whiteBall.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                whiteBall.x = poolTableBorder.validX(event.rawX - ballRadius, ballRadius * 2)
                whiteBall.y = poolTableBorder.validY(event.rawY - ballRadius, ballRadius * 2)
            }
            true
        }
    }
}