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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
    private val executor = Executors.newFixedThreadPool(3)

    private var running = false
    private var modeTouchListener: ModeTouchListener = ReadyModeTouchListener()

    private val mainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithNoStatusBar()
        addGlobalLayoutListener()

        subscribeUI()
        setViewListeners()
    }

    private fun setContentViewWithNoStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
    }

    private fun addGlobalLayoutListener() {
        parentLayout.viewTreeObserver.addOnGlobalLayoutListener {
            initDataInViewModel()
        }
    }

    private fun initDataInViewModel() {
        mainViewModel.apply {
            ballDiameter = whiteBallView.radius * 2
            setWhiteBallPosition(whiteBallView.x, whiteBallView.y)
            setBoundary(poolTableView.top, poolTableView.right, poolTableView.bottom, poolTableView.left)
        }
    }

    private fun subscribeUI() = with(mainViewModel) {
        val owner = this@MainActivity
        whiteBall.point.observe(owner, Observer {
            whiteBallView.x = it.x
            whiteBallView.y = it.y
            lineCanvas.removeLine()
        })
    }

    private fun setViewListeners() {
        setWhiteBallTouchListener()
        setLineCanvasTouchListener()
        setButtonClickListener()
    }

    private fun setWhiteBallTouchListener() {
        whiteBallView.setOnTouchListener { v, event ->
            modeTouchListener.onWhiteBallTouch(event)
        }
    }

    private fun setLineCanvasTouchListener() {
        poolTableView.setOnTouchListener { v, event ->
            modeTouchListener.onLineCanvasTouch(event)
        }
    }

    private fun setButtonClickListener() {
        button.setOnClickListener {
            if (!running) {
                lineCanvas.removeLine()
                startSimulation()
            } else {
                stopSimulation()
            }
        }
    }

    private fun startSimulation() {
        setPower()
        executeSimulation()
    }

    private fun setPower() = with(lineCanvas) {
        val ratio = line.length / MAX_LINE_LENGTH
        val slope = if (line.dx == 0f) 0f else line.dy / line.dx

        with(mainViewModel.whiteBall) {
            dx = getSign(line.dx) * sqrt((MAX_POWER * ratio) / (1 + slope.pow(2)))
            dy = slope * dx
        }
    }

    private fun getSign(dx: Float) = if (dx < 0) (-1) else 1

    private fun executeSimulation() {
        running = true
        button.text = getString(R.string.btn_stop)

        executor.submit {
            while(running) {
                mainViewModel.whiteBallUpdate()
                Thread.sleep(FRAME_DURATION_MS)
            }
        }
    }

    private fun stopSimulation() {
        running = false
        button.text = getString(R.string.btn_start)
    }

    inner class ReadyModeTouchListener : ModeTouchListener {
        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onLineCanvasTouch(event: MotionEvent): Boolean {
            lineCanvas.drawLine(whiteBallView.centerX, whiteBallView.centerY, event.rawX, event.rawY)

            return true
        }
    }

    inner class EditModeTouchListener : ModeTouchListener {
        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val x = event.rawX - whiteBallView.radius
                val y = event.rawY - whiteBallView.radius

                mainViewModel.whiteBallUpdate(x, y)
            }

            return true
        }

        override fun onLineCanvasTouch(event: MotionEvent) = false
    }

    inner class ExecuteModeTouchListener : ModeTouchListener {
        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onLineCanvasTouch(event: MotionEvent) = false
    }
}