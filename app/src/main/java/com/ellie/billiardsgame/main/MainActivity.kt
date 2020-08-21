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

/**
 * 당구 게임은 3가지 게임 모드로 동작한다. (편집 모드 - 준비 모드 - 실행 모드)
 *
 * 게임 모드 별로 바뀌어야 하는 UI나 Event Handler 동작 - GameModeUIEventHandler 추상 클래스에 정의한다.
 * 각 게임 모드 마다 추상 클래스의 구현체를 만들고, 게임 모드가 바뀌면 추상 클래스 인스턴스를 바꾸는 패턴.
 *
 * 게임 시작 모드는 [준비 모드]이다.
 */
@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {

    //----------------------------------------------------------
    // Instance data.
    //

    // 공을 칠 때 Fling 모션을 사용할 것인지에 대한 변수
    private var flingMode = false

    // 각 모드 별 정의한 클래스 인스턴스 생성
    private val readyModeUIEventHandler = ReadyModeUIEventHandler()
    private val editModeUIEventHandler = EditModeUIEventHandler()
    private val executeModeUIEventHandler = ExecuteModeUIEventHandler()

    // 현재 게임 모드 (Default = 준비 모드)
    private var gameModeUIEventHandler: GameModeUIEventHandler = readyModeUIEventHandler

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
        gameModeUIEventHandler = when (mode) {
            GameMode.READY -> readyModeUIEventHandler
            GameMode.EDIT -> editModeUIEventHandler
            GameMode.EXECUTE -> executeModeUIEventHandler
        }

        // 변경된 모드에 따라 Button UI 변경
        gameModeUIEventHandler.changeButtonUI()

        // 기존에 있던 안내선 지우기
        lineDrawer.removeLine()
    }

    /**
     * View에 리스너를 설정한다.
     */
    private fun setViewListeners() {
        whiteBallView.setOnTouchListener { v, event ->
            gameModeUIEventHandler.onWhiteBallTouch(event)
        }
        
        redBallView1.setOnTouchListener { v, event ->
            gameModeUIEventHandler.onRedBallTouch(v as BallView, event)
        }

        redBallView2.setOnTouchListener { v, event ->
            gameModeUIEventHandler.onRedBallTouch(v as BallView, event)
        }

        mainButton.setOnClickListener {
            gameModeUIEventHandler.onMainButtonClick()
        }

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
            lineDrawer.removeLine()
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
     * 게임 모드에 따라 화면 UI와 Event Handler를 정의한 인터페이스.
     */
    abstract inner class GameModeUIEventHandler {
        // 버튼 텍스트 리소스
        abstract val btnStringRes: Int

        // 버튼 색상 리소스
        abstract val btnColorRes: Int

        /**
         * 메인 버튼 클릭 시 Callback.
         */
        abstract fun onMainButtonClick()

        /**
         * 흰 공 터치 시 Callback.
         */
        abstract fun onWhiteBallTouch(event: MotionEvent): Boolean

        /**
         * 빨간 공 터치 시 Callback.
         */
        abstract fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean

        /**
         * 메인 버튼 UI를 변경한다.
         */
        fun changeButtonUI() {
            val context = this@MainActivity
            mainButton.run {
                text = context.getText(btnStringRes)
                setBackgroundColor(context.resources.getColor(btnColorRes, null))
            }
        }
    }

    /**
     * 준비 모드일 때 UI 및 Event Handler.
     */
    inner class ReadyModeUIEventHandler : GameModeUIEventHandler() {
        override val btnColorRes: Int
            get() = R.string.btn_shot

        override val btnStringRes: Int
            get() = R.color.colorShotButton

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
        
        /**
         * 버튼(Shot) 클릭 Callback.
         */
        override fun onMainButtonClick() {
            val velocity = lineDrawer.guideline.getVelocity()

            // 공의 속도가 0이 아닌 경우, 시뮬레이션 시작 & 실행 모드로 변경
            if (velocity.x * velocity.y != 0f) {
                mainViewModel.startSimulation(velocity)
                mainViewModel.changeGameMode(GameMode.EXECUTE)
            }
        }

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent): Boolean {
            redBallGestureDetector.onTouchEvent(event)

            return true
        }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            whiteBallGestureDetector.onTouchEvent(event)
            if (!flingMode) {
                // fling 모드가 아닌 경우, 안내선 보여주기
                lineDrawer.drawLine(whiteBallView.centerX, whiteBallView.centerY, event.rawX, event.rawY)
            }

            return true
        }
    }

    /**
     * 편집 모드일 때 UI 및 Event Handler.
     */
    inner class EditModeUIEventHandler : GameModeUIEventHandler() {
        override val btnColorRes: Int
            get() = R.string.btn_ok

        override val btnStringRes: Int
            get() = R.color.colorOKButton

        /**
         * 버튼(OK) 클릭 Callback.
         */
        override fun onMainButtonClick() {
            // 준비 모드로 변경
            mainViewModel.changeGameMode(GameMode.READY)
        }

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
    }

    /**
     * 실행 모드일 때 UI 및 Event Handler.
     */
    inner class ExecuteModeUIEventHandler : GameModeUIEventHandler() {
        override val btnColorRes: Int
            get() = R.string.btn_cancel

        override val btnStringRes: Int
            get() = R.color.colorCancelButton

        /**
         * 버튼(Cancel) 클릭 Callback.
         */
        override fun onMainButtonClick() {
            // 시뮬레이션 취소 (공들을 원위치로 되돌린다)
            mainViewModel.cancelSimulation()

            // 준비 모드로 변경
            mainViewModel.changeGameMode(GameMode.READY)
        }

        override fun onWhiteBallTouch(event: MotionEvent) = false

        override fun onRedBallTouch(ballView: BallView, event: MotionEvent) = false
    }
}