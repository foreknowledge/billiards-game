package com.ellie.billiardsgame.main

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
import com.ellie.billiardsgame.R
import com.ellie.billiardsgame.RED1
import com.ellie.billiardsgame.RED2
import com.ellie.billiardsgame.WHITE
import com.ellie.billiardsgame.customview.BallView
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
            updateBall(WHITE, whiteBallView.x, whiteBallView.y)
            updateBall(RED1, redBallView1.x, redBallView1.y)
            updateBall(RED2, redBallView2.x, redBallView2.y)
            setBoundary(poolTableView.top, poolTableView.right, poolTableView.bottom, poolTableView.left)
        }
    }

    private fun subscribeUI() = with(mainViewModel) {
        val owner = this@MainActivity
        whiteBallPosition.observe(owner, Observer {
            whiteBallView.x = it.x
            whiteBallView.y = it.y
        })
        redBall1Position.observe(owner, Observer {
            redBallView1.x = it.x
            redBallView1.y = it.y
        })
        redBall2Position.observe(owner, Observer {
            redBallView2.x = it.x
            redBallView2.y = it.y
        })
        curMode.observe(owner, Observer { applyChangedMode(it) })
    }

    private fun applyChangedMode(mode: GameMode) {
        modeActionConductor = when (mode) {
            GameMode.READY -> readyModeActionConductor
            GameMode.EDIT -> editModeActionConductor
            GameMode.EXECUTE -> executeModeActionConductor
        }

        button.text = modeActionConductor.btnText
        button.setBackgroundColor(modeActionConductor.btnColor)
        lineCanvas.removeLine()
    }

    private fun setViewListeners() {
        setWhiteBallTouchListener()
        setRedBallTouchListener()
        setButtonClickListener()
    }

    private fun setWhiteBallTouchListener() {
        whiteBallView.setOnTouchListener { v, event ->
            modeActionConductor.onWhiteBallTouch(event)
        }
    }

    private fun setRedBallTouchListener() {
        redBallView1.setOnTouchListener { v, event ->
            modeActionConductor.onRedBallTouch(v as BallView, event)
        }

        redBallView2.setOnTouchListener { v, event ->
            modeActionConductor.onRedBallTouch(v as BallView, event)
        }
    }

    private fun setButtonClickListener() {
        button.setOnClickListener {
            modeActionConductor.onButtonClick()
        }
    }

    interface ModeActionConductor {
        val btnText: String
        val btnColor: Int
        fun onWhiteBallTouch(event: MotionEvent): Boolean
        fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean
        fun onButtonClick()
    }

    inner class ReadyModeActionConductor : ModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_shot).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorReadyButton, null) }

        private val gestureDetector by lazy {
            GestureDetectorCompat(this@MainActivity, object : GestureDetector.SimpleOnGestureListener() {
                override fun onLongPress(e: MotionEvent?) {
                    mainViewModel.changeMode(GameMode.EDIT)
                }
            })
        }

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(event)

            return true
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
                mainViewModel.changeMode(GameMode.EXECUTE)
            }
        }
    }

    inner class EditModeActionConductor : ModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_ok).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorEditButton, null) }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val x = event.rawX - whiteBallView.radius
                val y = event.rawY - whiteBallView.radius

                with(mainViewModel) {
                    updateAvailablePosition(WHITE, x, y)
                }
            }

            return true
        }

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val x = event.rawX - ballView.radius
                val y = event.rawY - ballView.radius

                with(mainViewModel) {
                    if (ballView.id == R.id.redBallView1) {
                        updateAvailablePosition(RED1, x, y)
                    } else {
                        updateAvailablePosition(RED2, x, y)
                    }
                }
            }

            return true
        }

        override fun onButtonClick() {
            mainViewModel.changeMode(GameMode.READY)
        }
    }

    inner class ExecuteModeActionConductor : ModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_end).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorShotButton, null) }

        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent) = false

        override fun onButtonClick() {
            mainViewModel.endSimulationAndRestorePositions()
            mainViewModel.changeMode(GameMode.READY)
        }
    }
}