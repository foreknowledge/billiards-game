package com.ellie.billiardsgame.main

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ellie.billiardsgame.*
import com.ellie.billiardsgame.databinding.ActivityMainBinding
import com.ellie.billiardsgame.model.Point
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

    // 각 모드 별 UI Event Handler 인스턴스
    private val readyState = ReadyState()
    private val editState = EditState()
    private val executeState = ExecuteState()

    // 현재 게임 모드 UI Event Handler (Default = 준비 모드)
    private var state: State = readyState

    private val mainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    //----------------------------------------------------------
    // Public interface.
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityMainBinding 초기화
        initActivityMainBinding()
        // View가 화면에 그려졌을 때 리스너 추가
        addGlobalLayoutListener()

        // ViewModel의 데이터 변경 관찰(Observing)
        observeViewModelData()
        // View 리스너 정의
        setViewListeners()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            WindowInsetsControllerCompat(window, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * Data Binding 인스턴스를 초기화한다.
     */
    private fun initActivityMainBinding() {
        binding.lifecycleOwner = this
        binding.run {
            viewModel = mainViewModel
        }
    }

    /**
     * View가 화면에 그려졌을 때 콜백 리스너를 추가한다.
     */
    private fun addGlobalLayoutListener() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // ViewModel의 데이터 초기화
                initDataInViewModel()
            }
        })
    }

    /**
     * ViewModel의 데이터를 초기화한다.
     */
    private fun initDataInViewModel() {
        mainViewModel.run {
            // 공의 위치 초기화
            with(binding) {
                whiteBall.update(Point(whiteBallView.x, whiteBallView.y))
                redBall1.update(Point(redBallView1.x, redBallView1.y))
                redBall2.update(Point(redBallView2.x, redBallView2.y))
            }

            // 당구대 경계 초기화
            with (binding.poolTableView) {
                setBoundary(top, right, bottom, left)
            }
        }
    }

    /**
     * ViewModel의 데이터 변경을 감지해 콜백을 수행하는 Observer를 등록한다.
     */
    private fun observeViewModelData() = with(mainViewModel) {
        // 게임 모드가 변경되면 모드에 따른 UI 변경
        curGameMode.observe(this@MainActivity, { changeState(it) })
    }

    /**
     * 변경된 게임 모드에 따라 UI Event Handler를 변경한다.
     */
    private fun changeState(mode: GameMode) {
        state = when (mode) {
            GameMode.READY -> readyState
            GameMode.EDIT -> editState
            GameMode.EXECUTE -> executeState
        }

        // 변경된 모드에 따라 Button UI 변경
        state.changeButtonUI()

        // 기존에 있던 안내선 지우기
        binding.lineDrawer.removeLine()
    }

    /**
     * View에 리스너를 설정한다.
     */
    private fun setViewListeners() {
        // 흰 공 터치 리스너 설정
        binding.whiteBallView.setOnTouchListener { _, event ->
            state.onWhiteBallTouch(event)
        }

        // 빨간 공 터치 리스너 설정
        binding.redBallView1.setOnTouchListener { v, event ->
            state.onRedBallTouch(v, event)
        }

        // 빨간 공 터치 리스너 설정
        binding.redBallView2.setOnTouchListener { v, event ->
            state.onRedBallTouch(v, event)
        }

        // 메인 버튼 클릭 리스너 설정
        binding.mainButton.setOnClickListener {
            state.onMainButtonClick()
        }

        // Fling 버튼 클릭 리스너 설정
        binding.flingButton.setOnClickListener {
            // toggle
            flingMode = !flingMode

            // Fling Mode에 따라 Fling 버튼의 UI 변경
            if (flingMode) {
                changFlingButtonState(R.string.btn_fling_on, getColor(R.color.colorOnButton))
            } else {
                changFlingButtonState(R.string.btn_fling_off, getColor(R.color.colorDefaultButton))
            }

            // 기존의 안내선을 지운다.
            binding.lineDrawer.removeLine()
        }
    }

    /**
     * Fling 버튼의 텍스트, 버튼 색상을 바꾼다.
     */
    private fun changFlingButtonState(@StringRes textResId: Int, @ColorInt color: Int) {
        binding.flingButton.run {
            text = getText(textResId)
            setBackgroundColor(color)
        }
    }

    //----------------------------------------------------------
    // Inner class.
    // - GameMode 별 UI Event Handler 정의
    //

    /**
     * 게임 모드에 따라 변경할 화면 UI Event Handler를 정의한 추상 클래스.
     */
    abstract inner class State {
        // 메인 버튼 텍스트
        abstract val mainBtnText: String

        // 메인 버튼 색상
        abstract val mainBtnColor: Int

        /**
         * 메인 버튼 클릭 시 호출된다.
         */
        abstract fun onMainButtonClick()

        /**
         * 흰 공 터치 시 호출된다.
         */
        abstract fun onWhiteBallTouch(event: MotionEvent): Boolean

        /**
         * 빨간 공 터치 시 호출된다.
         */
        abstract fun onRedBallTouch(ballView: View, event: MotionEvent): Boolean

        /**
         * 메인 버튼 UI를 변경한다.
         */
        fun changeButtonUI() {
            binding.mainButton.run {
                text = mainBtnText
                setBackgroundColor(mainBtnColor)
            }
        }
    }

    /**
     * 준비 모드일 때 UI Event Handler.
     */
    inner class ReadyState : State() {

        //----------------------------------------------------------
        // Instance data.
        //

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
                 * 초기 속도가 최대 속도를 넘어가면 제한한다.
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

        //----------------------------------------------------------
        // Public interface.
        //

        override val mainBtnText: String by lazy { this@MainActivity.getString(R.string.btn_shot) }
        override val mainBtnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorShotButton, null) }

        /**
         * 버튼(Shot) 클릭 Callback.
         */
        override fun onMainButtonClick() {
            // 안내선의 길이, 방향에 따라 초기 공 속도 계산
            val velocity = binding.lineDrawer.getVelocity()

            // 공의 속도가 0이 아닌 경우, 시뮬레이션 시작 & 실행 모드로 변경
            if (velocity.x * velocity.y != 0f) {
                mainViewModel.startSimulation(velocity)
                mainViewModel.changeGameMode(GameMode.EXECUTE)
            }
        }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            whiteBallGestureDetector.onTouchEvent(event)
            if (!flingMode) {
                // fling 모드가 아닌 경우, 안내선 보여주기
                val whiteBallCenter = Point(
                    mainViewModel.whiteBall.centerX,
                    mainViewModel.whiteBall.centerY
                )
                binding.lineDrawer.drawLine(whiteBallCenter.x, whiteBallCenter.y, event.rawX, event.rawY)
            }

            return true
        }

        override fun onRedBallTouch(ballView: View, event: MotionEvent): Boolean {
            redBallGestureDetector.onTouchEvent(event)

            return true
        }
    }

    /**
     * 편집 모드일 때 UI Event Handler.
     */
    inner class EditState : State() {

        //----------------------------------------------------------
        // Public interface.
        //

        override val mainBtnText: String by lazy { this@MainActivity.getString(R.string.btn_ok) }
        override val mainBtnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorOKButton, null) }

        /**
         * 버튼(OK) 클릭 Callback.
         */
        override fun onMainButtonClick() {
            // 준비 모드로 변경
            mainViewModel.changeGameMode(GameMode.READY)
        }

        override fun onWhiteBallTouch(event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val ballRadius = mainViewModel.whiteBall.radius
                val x = event.rawX - ballRadius
                val y = event.rawY - ballRadius

                // 터치 위치에 따라 공 위치 변경
                with(mainViewModel) {
                    whiteBall.updatePosition(x, y)
                }
            }

            return true
        }

        override fun onRedBallTouch(ballView: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_MOVE) {
                val ballRadius = mainViewModel.whiteBall.radius
                val x = event.rawX - ballRadius
                val y = event.rawY - ballRadius

                // 터치 위치에 따라 해당 공 위치 변경
                with(mainViewModel) {
                    if (ballView.id == R.id.redBallView1)
                        redBall1.updatePosition(x, y)
                    else
                        redBall2.updatePosition(x, y)
                }
            }

            return true
        }
    }

    /**
     * 실행 모드일 때 UI Event Handler.
     */
    inner class ExecuteState : State() {

        //----------------------------------------------------------
        // Public interface.
        //

        override val mainBtnText: String by lazy { this@MainActivity.getString(R.string.btn_cancel) }
        override val mainBtnColor: Int by lazy { this@MainActivity.resources.getColor(R.color.colorCancelButton, null) }

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

        override fun onRedBallTouch(ballView: View, event: MotionEvent) = false
    }
}