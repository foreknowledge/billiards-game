package com.ellie.billiardsgame.main

import android.graphics.Rect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ellie.billiardsgame.*
import com.ellie.billiardsgame.model.Ball
import com.ellie.billiardsgame.model.Boundary
import com.ellie.billiardsgame.model.Point
import java.util.concurrent.Executors
import kotlin.math.abs

/**
 * View를 위한 데이터를 조작한다.
 */
class MainViewModel : ViewModel() {
    private val _curGameMode = MutableLiveData(GameMode.READY)
    val curGameMode: LiveData<GameMode> = _curGameMode

    // 스레드를 실행하는 executor
    private val executor = Executors.newFixedThreadPool(3)

    // 시뮬레이션 실행을 제어하는 변수
    private var isSimulating = false

    // 당구공들을 관리하는 배열
    private val balls = listOf(Ball(), Ball(), Ball())

    // 당구공들의 시작 위치를 가지고 있는 배열
    private val startBallPositions = listOf(Point(), Point(), Point())

    // 공의 충돌 관련 작업을 담당
    private val collisionManager = BallCollisionManager(balls)

    val whiteBall: Ball = balls[WHITE]
    val redBall1: Ball = balls[RED1]
    val redBall2: Ball = balls[RED2]

    /**
     * 당구대의 Boundary 데이터를 설정한다.
     */
    fun setBoundary(top: Int, right: Int, bottom: Int, left: Int) {
        collisionManager.setBoundary(Boundary(Rect(left, top, right, bottom)))
    }

    /**
     * 당구공의 위치를 업데이트 한다.
     * @param ballId ball을 구별하는 id값
     */
    fun updateBall(ballId: Int, x: Float, y: Float) {
        balls[ballId].update(Point(x, y))
    }

    /**
     * 당구공의 위치를 충돌을 고려해 업데이트한다.
     */
    fun updateBallPosition(ballId: Int, x: Float, y: Float) {
        val nextPoint = collisionManager.getAdjustedPointToBound(ballId, x, y)

        val collidedBallId = collisionManager.getCollidedBallId(ballId, nextPoint)
        if (collidedBallId != null){
            // 충돌한 경우
            val (velocity1, velocity2) = CollisionCalculator.calculateBallsVelocity(balls[ballId], balls[collidedBallId])
            balls[ballId].setVelocity(velocity1.x, velocity1.y)
            balls[collidedBallId].setVelocity(velocity2.x, velocity2.y)
        } else {
            // 충돌하지 않은 경우
            balls[ballId].update(nextPoint)
        }
    }

    /**
     * 게임 모드를 변경한다.
     */
    fun changeGameMode(gameMode: GameMode) {
        _curGameMode.postValue(gameMode)
    }

    /**
     * 당구 시뮬레이션을 시작한다.
     */
    fun startSimulation(velocity: Point) {
        // 공의 초기 속도 설정
        initAllBallsVelocity(velocity)
        // 공들의 시작 위치 저장
        captureBallPositions()

        // 스레드 실행
        executor.submit {
            isSimulating = true

            // FRAME_DURATION_MS 마다 화면을 갱신
            while(isSimulating) {
                // 모든 공을 움직인다
                moveAllBalls()

                // 모든 공이 멈추었으면 시뮬레이션 종료
                if (noMovingBall()) {
                    isSimulating = false
                    changeGameMode(GameMode.READY)
                }

                Thread.sleep(FRAME_DURATION_MS)
            }
        }
    }

    /**
     * 시뮬레이션을 취소한다.
     */
    fun cancelSimulation() {
        // 시뮬레이션 종료
        isSimulating = false

        // 공들을 시작 위치로 되돌리기
        restoreBallPositions()
    }

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * 공들의 시작 속도를 설정한다.
     */
    private fun initAllBallsVelocity(velocity: Point) {
        // 흰 공은 시작 속도를 가진 상태
        whiteBall.setVelocity(velocity.x, velocity.y)

        // 빨간 공은 멈춰 있는 상태
        redBall1.setVelocity(0f, 0f)
        redBall2.setVelocity(0f, 0f)
    }

    /**
     * 현재 공들의 위치를 저장한다.
     */
    private fun captureBallPositions() {
        for (i in balls.indices) {
            startBallPositions[i].update(balls[i].x, balls[i].y)
        }
    }

    /**
     * 모든 공을 움직인다.
     */
    private fun moveAllBalls() {
        for (i in balls.indices) {
            with (balls[i]) {
                decreaseVelocity()
                updateBallPosition(i, nextX, nextY)
            }
        }
    }

    /**
     * 모든 공이 멈추었는지 확인한다.
     */
    private fun noMovingBall(): Boolean {
        balls.forEach { ball ->
            if (abs(ball.dx) >= STOP_THRESHOLD || abs(ball.dy) >= STOP_THRESHOLD) {
                // 하나의 공이라도 속도가 stop Threshold 보다 크다면 모든 공이 멈춘 것이 아님
                return false
            }
        }

        // 모든 공의 속도가 멈추었다고 판단하고 true 반환
        return true
    }

    /**
     * 모든 공을 원 위치로 되돌린다.
     */
    private fun restoreBallPositions() {
        for (i in balls.indices) {
            balls[i].update(startBallPositions[i])
        }
    }
}