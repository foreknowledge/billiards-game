package com.ellie.billiardsgame.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ellie.billiardsgame.*
import com.ellie.billiardsgame.customview.BallView
import com.ellie.billiardsgame.model.Point
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.hypot

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
    private var flingOn = false

    private val readyModeActionConductor = ReadyModeActionConductor()
    private val editModeActionConductor = EditModeActionConductor()
    private val executeModeActionConductor = ExecuteModeActionConductor()

    private var gameModeActionConductor: GameModeActionConductor = readyModeActionConductor

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
        curGameMode.observe(owner, Observer { applyChangedMode(it) })
    }

    private fun applyChangedMode(mode: GameMode) {
        gameModeActionConductor = when (mode) {
            GameMode.READY -> readyModeActionConductor
            GameMode.EDIT -> editModeActionConductor
            GameMode.EXECUTE -> executeModeActionConductor
        }

        modeButton.text = gameModeActionConductor.btnText
        modeButton.setBackgroundColor(gameModeActionConductor.btnColor)
        lineCanvas.removeLine()
    }

    private fun setViewListeners() {
        setWhiteBallTouchListener()
        setRedBallTouchListener()
        setModeButtonClickListener()
        setFlingButtonClickListener()
    }

    private fun setWhiteBallTouchListener() {
        whiteBallView.setOnTouchListener { v, event ->
            gameModeActionConductor.onWhiteBallTouch(event)
        }
    }

    private fun setRedBallTouchListener() {
        redBallView1.setOnTouchListener { v, event ->
            gameModeActionConductor.onRedBallTouch(v as BallView, event)
        }

        redBallView2.setOnTouchListener { v, event ->
            gameModeActionConductor.onRedBallTouch(v as BallView, event)
        }
    }

    private fun setModeButtonClickListener() {
        modeButton.setOnClickListener {
            gameModeActionConductor.onModeButtonClick()
        }
    }

    private fun setFlingButtonClickListener() {
        flingButton.setOnClickListener {
            flingOn = !flingOn

            if (flingOn) {
                val btnColor = getColor(R.color.colorOnButton)
                changFlingButtonState(R.string.btn_fling_on, btnColor)
            } else {
                val btnColor = getColor(R.color.colorDefaultButton)
                changFlingButtonState(R.string.btn_fling_off, btnColor)
            }

            lineCanvas.removeLine()
        }
    }

    private fun changFlingButtonState(@StringRes textResId: Int, @ColorInt color: Int) {
        flingButton.text = getText(textResId)
        flingButton.setBackgroundColor(color)
    }

    interface GameModeActionConductor {
        val btnText: String
        val btnColor: Int
        fun onWhiteBallTouch(event: MotionEvent): Boolean
        fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean
        fun onModeButtonClick()
    }

    inner class ReadyModeActionConductor : GameModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_shot).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorReadyButton, null) }

        private val redBallGestureDetector by lazy {
            GestureDetectorCompat(this@MainActivity, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    mainViewModel.changeGameMode(GameMode.EDIT)
                    return true
                }
            })
        }

        private val whiteBallGestureDetector by lazy {
            GestureDetectorCompat(this@MainActivity, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    mainViewModel.changeGameMode(GameMode.EDIT)
                    return true
                }

                override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                    if (flingOn) {
                        val velocityPerFrame = Point(velocityX, velocityY).times(0.001f * FRAME_DURATION_MS)

                        val velocitySize = hypot(velocityPerFrame.x, velocityPerFrame.y)
                        if (velocitySize > MAX_POWER) {
                            val ratio = MAX_POWER / velocitySize
                            velocityPerFrame.x *= ratio
                            velocityPerFrame.y *= ratio
                        }

                        mainViewModel.startSimulation(Point(velocityPerFrame.x, velocityPerFrame.y))
                        mainViewModel.changeGameMode(GameMode.EXECUTE)
                    }

                    return true
                }
            })
        }

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean {
            redBallGestureDetector.onTouchEvent(event)

            return true
        }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            whiteBallGestureDetector.onTouchEvent(event)
            if (!flingOn) {
                lineCanvas.drawLine(whiteBallView.centerX, whiteBallView.centerY, event.rawX, event.rawY)
            }

            return true
        }

        override fun onModeButtonClick() {
            val velocity = lineCanvas.getVelocity()

            if (velocity.x * velocity.y != 0f) {
                mainViewModel.startSimulation(velocity)
                mainViewModel.changeGameMode(GameMode.EXECUTE)
            }
        }
    }

    inner class EditModeActionConductor : GameModeActionConductor {
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

        override fun onModeButtonClick() {
            mainViewModel.changeGameMode(GameMode.READY)
        }
    }

    inner class ExecuteModeActionConductor : GameModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_end).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorShotButton, null) }

        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent) = false

        override fun onModeButtonClick() {
            mainViewModel.endSimulationAndRestorePositions()
            mainViewModel.changeGameMode(GameMode.READY)
        }
    }
}