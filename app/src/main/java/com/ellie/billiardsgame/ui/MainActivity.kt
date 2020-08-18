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
import com.ellie.billiardsgame.BilliardsMode
import com.ellie.billiardsgame.R
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
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
            setRedBall1Position(redBallView1.x, redBallView1.y)
            setRedBall2Position(redBallView2.x, redBallView2.y)
            setBoundary(poolTableView.top, poolTableView.right, poolTableView.bottom, poolTableView.left)
        }
    }

    private fun subscribeUI() = with(mainViewModel) {
        val owner = this@MainActivity
        whiteBall.point.observe(owner, Observer {
            whiteBallView.x = it.x
            whiteBallView.y = it.y
        })
        redBall1.point.observe(owner, Observer {
            redBallView1.x = it.x
            redBallView1.y = it.y
        })
        redBall2.point.observe(owner, Observer {
            redBallView2.x = it.x
            redBallView2.y = it.y
        })
        curMode.observe(owner, Observer { applyChangedMode(it) })
    }

    private fun applyChangedMode(mode: BilliardsMode) {
        modeActionConductor = when (mode) {
            BilliardsMode.READY -> readyModeActionConductor
            BilliardsMode.EDIT -> editModeActionConductor
            BilliardsMode.EXECUTE -> executeModeActionConductor
        }

        button.text = modeActionConductor.btnText
        lineCanvas.removeLine()
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
                    mainViewModel.changeMode(BilliardsMode.EDIT)
                }
            })
        }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(event)
            lineCanvas.drawLine(whiteBallView.centerX, whiteBallView.centerY, event.rawX, event.rawY)

            return true
        }

        override fun onButtonClick() {
            val velocity = lineCanvas.line.getVelocity()

            if (velocity.x * velocity.y != 0f) {
                mainViewModel.startSimulation(velocity)
                mainViewModel.changeMode(BilliardsMode.EXECUTE)
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
            mainViewModel.changeMode(BilliardsMode.READY)
        }
    }

    inner class ExecuteModeActionConductor : ModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_end).toString() }

        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onButtonClick() {
            mainViewModel.stopSimulation()
            mainViewModel.changeMode(BilliardsMode.READY)
        }
    }
}