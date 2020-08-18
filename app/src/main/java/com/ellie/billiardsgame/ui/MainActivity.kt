package com.ellie.billiardsgame.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ellie.billiardsgame.*
import com.ellie.billiardsgame.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
    private var running = false
    private val executor = Executors.newFixedThreadPool(3)

    private val mainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithNoStatusBar()

        subscribeUI()
        setViewListeners()
    }

    private fun setContentViewWithNoStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
    }

    private fun subscribeUI() = with(mainViewModel) {
        val owner = this@MainActivity
        whiteBall.point.observe(owner, Observer {
            whiteBallView.x = it.x
            whiteBallView.y = it.y
            poolTableView.removeLine()
        })
    }

    private fun setViewListeners() {
        setWhiteBallTouchListener()
        setPoolTableTouchListener()
        setButtonClickListener()
    }

    private fun setWhiteBallTouchListener() {
        whiteBallView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                val x = event.rawX - whiteBallView.radius
                val y = event.rawY - whiteBallView.radius

                mainViewModel.whiteBallUpdate(x, y)
            }

            true
        }

        whiteBallView.viewTreeObserver.addOnGlobalLayoutListener {
            initViewModel()
        }
    }

    private fun initViewModel() {
        mainViewModel.apply {
            ballDiameter = whiteBallView.radius * 2
            setWhiteBallPosition(whiteBallView.x, whiteBallView.y)
            setBoundary(poolTableView.top, poolTableEdge.right, poolTableView.bottom, poolTableView.left)
        }
    }

    private fun setPoolTableTouchListener() {
        poolTableView.setOnTouchListener { v, event ->
            poolTableView.drawLine(whiteBallView.centerX, whiteBallView.centerY, event.rawX, event.rawY)

            true
        }
    }

    private fun setButtonClickListener() {
        button.setOnClickListener {
            if (!running) {
                poolTableView.removeLine()
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
                mainViewModel.whiteBallUpdate()
                Thread.sleep(FRAME_DURATION_MS)
            }
        }
    }

    private fun stopSimulation() {
        running = false
    }
}