package com.ellie.billiardsgame

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
    private var running = false
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithNoStatusBar()

        setButtonClickListener()
    }

    private fun setContentViewWithNoStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
    }

    private fun setButtonClickListener() {
        button.setOnClickListener {
            if (!running) {
                startSimulation()
                button.text = getString(R.string.btn_stop)
            } else {
                stopSimulation()
                button.text = getString(R.string.btn_start)
            }
        }
    }

    private fun startSimulation() {
        running = true

        executor.submit {
            while(running) {
                whiteBall.move()
                Thread.sleep(10)
            }
        }
    }

    private fun stopSimulation() {
        running = false
    }
}