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

    //----------------------------------------------------------
    // Instance data.
    //

    // 공을 칠 때 Fling을 사용할 것인지에 대한 변수
    private var flingMode = false

    // 각 모드에 실행할 동작 정의
    private val readyModeActionConductor = ReadyModeActionConductor()
    private val editModeActionConductor = EditModeActionConductor()
    private val executeModeActionConductor = ExecuteModeActionConductor()

    // 현재 게임 모드 (Default = 준비 모드)
    private var gameModeActionConductor: GameModeActionConductor = readyModeActionConductor

    private val mainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    //----------------------------------------------------------
    // Public interface.
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Status Bar 없는 화면으로 실행
        setContentViewWithNoStatusBar()
        // View가 화면에 그려졌을 때 리스너 추가
        addGlobalLayoutListener()

        // ViewModel의 데이터 변경 관찰(Observing)
        observeViewModelData()
        // View 리스너 정의
        setViewListeners()
    }

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * Status Bar 없는 화면을 만든다.
     */
    private fun setContentViewWithNoStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_main)
    }

    /**
     * View가 화면에 그려졌을 때 콜백 리스너를 추가한다.
     */
    private fun addGlobalLayoutListener() {
        parentLayout.viewTreeObserver.addOnGlobalLayoutListener {
            // ViewModel의 데이터 초기화
            initDataInViewModel()
        }
    }

    /**
     * ViewModel의 데이터를 초기화한다.
     */
    private fun initDataInViewModel() {
        mainViewModel.apply {
            // 공의 위치 초기화
            updateBall(WHITE, whiteBallView.x, whiteBallView.y)
            updateBall(RED1, redBallView1.x, redBallView1.y)
            updateBall(RED2, redBallView2.x, redBallView2.y)

            // 당구대 경계 초기화
            setBoundary(poolTableView.top, poolTableView.right, poolTableView.bottom, poolTableView.left)
        }
    }

    /**
     * ViewModel의 데이터 변경을 감지해 콜백을 수행하는 Observer를 등록한다.
     */
    private fun observeViewModelData() = with(mainViewModel) {
        val owner = this@MainActivity

        // 각 공의 위치가 변경되면 View에도 변경한 위치 적용
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

        // 게임 모드가 변경되면 모드에 따른 UI 변경
        curGameMode.observe(owner, Observer { applyChangedMode(it) })
    }

    /**
     * 변경된 게임 모드에 따라 UI와 UI 동작을 변경한다.
     */
    private fun applyChangedMode(mode: GameMode) {
        gameModeActionConductor = when (mode) {
            GameMode.READY -> readyModeActionConductor
            GameMode.EDIT -> editModeActionConductor
            GameMode.EXECUTE -> executeModeActionConductor
        }

        modeButton.text = gameModeActionConductor.btnText
        modeButton.setBackgroundColor(gameModeActionConductor.btnColor)

        // 기존에 있던 안내선을 지운다.
        lineCanvas.removeLine()
    }

    /**
     * View에 리스너를 설정한다.
     */
    private fun setViewListeners() {
        setWhiteBallTouchListener()
        setRedBallTouchListener()
        setModeButtonClickListener()
        setFlingButtonClickListener()
    }

    /**
     * 흰 공의 터치 리스너를 설정한다.
     */
    private fun setWhiteBallTouchListener() {
        whiteBallView.setOnTouchListener { v, event ->
            // 현재 모드에 맞는 흰 공 터치 이벤트 발생
            gameModeActionConductor.onWhiteBallTouch(event)
        }
    }

    /**
     * 빨간 공의 터치 리스너를 설정한다.
     */
    private fun setRedBallTouchListener() {
        redBallView1.setOnTouchListener { v, event ->
            // 현재 모드에 맞는 빨간 공 터치 이벤트 발생
            gameModeActionConductor.onRedBallTouch(v as BallView, event)
        }

        redBallView2.setOnTouchListener { v, event ->
            // 현재 모드에 맞는 빨간 공 터치 이벤트 발생
            gameModeActionConductor.onRedBallTouch(v as BallView, event)
        }
    }

    /**
     * 게임 모드 버튼의 클릭 리스너를 설정한다.
     */
    private fun setModeButtonClickListener() {
        modeButton.setOnClickListener {
            // 현재 모드에 맞는 게임 모드 클릭 이벤트 발생
            gameModeActionConductor.onModeButtonClick()
        }
    }

    /**
     * Fling 버튼의 클릭 리스너를 설정한다.
     */
    private fun setFlingButtonClickListener() {
        flingButton.setOnClickListener {
            // toggle
            flingMode = !flingMode

            // Fling Mode에 따라 Fling 버튼의 UI 변경
            if (flingMode) {
                changFlingButtonState(R.string.btn_fling_on, getColor(R.color.colorOnButton))
            } else {
                changFlingButtonState(R.string.btn_fling_off, getColor(R.color.colorDefaultButton))
            }

            // 기존의 안내선을 지운다.
            lineCanvas.removeLine()
        }
    }

    /**
     * Fling 버튼의 텍스트, 버튼 색상을 바꾼다.
     */
    private fun changFlingButtonState(@StringRes textResId: Int, @ColorInt color: Int) {
        flingButton.text = getText(textResId)
        flingButton.setBackgroundColor(color)
    }

    /**
     * 게임 모드에 따라 적절한 동작을 하는 인터페이스.
     */
    interface GameModeActionConductor {
        val btnText: String
        val btnColor: Int
        fun onWhiteBallTouch(event: MotionEvent): Boolean
        fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean
        fun onModeButtonClick()
    }

    /**
     * 준비 모드일 때 실행할 동작 정의
     */
    inner class ReadyModeActionConductor : GameModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_shot).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorReadyButton, null) }

        // 빨간 공 Gesture Detector
        private val redBallGestureDetector by lazy {
            GestureDetectorCompat(this@MainActivity, object: GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    mainViewModel.changeGameMode(GameMode.EDIT)
                    return true
                }
            })
        }

        // 흰 공 Gesture Detector
        private val whiteBallGestureDetector by lazy {
            GestureDetectorCompat(this@MainActivity, object: GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent?): Boolean {
                    mainViewModel.changeGameMode(GameMode.EDIT)
                    return true
                }

                override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                    // Fling 모드일 때만 동작
                    if (flingMode) {
                        // 입력 velocity = 1초동안 움직인 픽셀 (px / sec)
                        // 프레임 간격에 따라 단위 변환 (10ms 동안 움직인 픽셀)
                        val velocityPerFrame = Point(velocityX, velocityY).times(0.001f * FRAME_DURATION_MS)

                        // 최대 속도로 제한
                        applyMaxVelocity(velocityPerFrame)

                        // 시뮬레이션 시작 & 실행 모드로 변경
                        mainViewModel.startSimulation(Point(velocityPerFrame.x, velocityPerFrame.y))
                        mainViewModel.changeGameMode(GameMode.EXECUTE)
                    }

                    return true
                }

                /**
                 * 최대 속도로 제한한다.
                 */
                private fun applyMaxVelocity(velocity: Point) {
                    val velocitySize = hypot(velocity.x, velocity.y)
                    if (velocitySize > MAX_POWER) {
                        // 최대 속도를 넘어가면 비율에 따른 x, y 속도 계산
                        val ratio = MAX_POWER / velocitySize
                        velocity.x *= ratio
                        velocity.y *= ratio
                    }
                }
            })
        }

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean {
            redBallGestureDetector.onTouchEvent(event)

            return true
        }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            whiteBallGestureDetector.onTouchEvent(event)
            if (!flingMode) {
                // fling 모드가 아닌 경우, 안내선 보여주기
                lineCanvas.drawLine(whiteBallView.centerX, whiteBallView.centerY, event.rawX, event.rawY)
            }

            return true
        }

        override fun onModeButtonClick() {
            val velocity = lineCanvas.getVelocity()

            // 공의 속도가 0이 아닌 경우, 시뮬레이션 시작 & 실행 모드로 변경
            if (velocity.x * velocity.y != 0f) {
                mainViewModel.startSimulation(velocity)
                mainViewModel.changeGameMode(GameMode.EXECUTE)
            }
        }
    }

    /**
     * 편집 모드일 때 실행할 동작 정의
     */
    inner class EditModeActionConductor : GameModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_ok).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorEditButton, null) }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val x = event.rawX - whiteBallView.radius
                val y = event.rawY - whiteBallView.radius

                // 터치 위치에 따라 공 위치 변경
                with(mainViewModel) {
                    updateBallPosition(WHITE, x, y)
                }
            }

            return true
        }

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val x = event.rawX - ballView.radius
                val y = event.rawY - ballView.radius

                // 터치 위치에 따라 해당 공 위치 변경
                with(mainViewModel) {
                    if (ballView.id == R.id.redBallView1) {
                        updateBallPosition(RED1, x, y)
                    } else {
                        updateBallPosition(RED2, x, y)
                    }
                }
            }

            return true
        }

        override fun onModeButtonClick() {
            // 준비 모드로 변경
            mainViewModel.changeGameMode(GameMode.READY)
        }
    }

    /**
     * 실행 모드일 때 실행할 동작 정의
     */
    inner class ExecuteModeActionConductor : GameModeActionConductor {
        override val btnText: String by lazy { this@MainActivity.getText(R.string.btn_end).toString() }
        override val btnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorShotButton, null) }

        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent) = false

        override fun onModeButtonClick() {
            // 시뮬레이션을 종료하고 공들을 원위치로 되돌리기
            mainViewModel.endSimulationAndRestorePositions()

            // 준비 모드로 변경
            mainViewModel.changeGameMode(GameMode.READY)
        }
    }
}