package com.ellie.billiardsgame.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
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
    private val readyModeActionConductor = ReadyModeActionConductor()
    private val editModeActionConductor = EditModeActionConductor()
    private val executeModeActionConductor = ExecuteModeActionConductor()

    private var modeActionConductor: ModeActionConductor = readyModeActionConductor

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
        })
    }

    private fun setViewListeners() {
        setWhiteBallTouchListener()
        setButtonClickListener()
    }

    private fun setWhiteBallTouchListener() {
        whiteBallView.setOnTouchListener { v, event ->
            modeActionConductor.onWhiteBallTouch(event)
        }
    }

    private fun setButtonClickListener() {
        button.setOnClickListener {
            modeActionConductor.onButtonClick()
        }
    }

    interface ModeActionConductor {
        val btnText: String
        fun onWhiteBallTouch(event: MotionEvent): Boolean
        fun onButtonClick()
    }

    inner class ReadyModeActionConductor : ModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_shot).toString() }

        private val gestureDetector by lazy {
            GestureDetectorCompat(this@MainActivity, object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent?) {
                    changeMode(BilliardsMode.EDIT)
                }
            })
        }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(event)
            lineCanvas.drawLine(whiteBallView.centerX, whiteBallView.centerY, event.rawX, event.rawY)

            return true
        }

        override fun onButtonClick() {
            startSimulation()
            changeMode(BilliardsMode.EXECUTE)
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
            button.text = getString(R.string.btn_end)

            executor.submit {
                while(running) {
                    mainViewModel.whiteBallUpdate()
                    Thread.sleep(FRAME_DURATION_MS)
                }
            }
        }
    }

    inner class EditModeActionConductor : ModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_ok).toString() }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val x = event.rawX - whiteBallView.radius
                val y = event.rawY - whiteBallView.radius

                mainViewModel.whiteBallUpdate(x, y)
            }

            return true
        }

        override fun onButtonClick() {
            changeMode(BilliardsMode.READY)
        }
    }

    inner class ExecuteModeActionConductor : ModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_end).toString() }

        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onButtonClick() {
            stopSimulation()
            changeMode(BilliardsMode.READY)
        }

        private fun stopSimulation() {
            running = false
            button.text = getString(R.string.btn_shot)
        }
    }

    private fun changeMode(mode: BilliardsMode) {
        modeActionConductor = when (mode) {
            BilliardsMode.READY -> readyModeActionConductor
            BilliardsMode.EDIT -> editModeActionConductor
            BilliardsMode.EXECUTE -> executeModeActionConductor
        }

        button.text = modeActionConductor.btnText
        lineCanvas.removeLine()
    }
}