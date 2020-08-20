package com.ellie.billiardsgame.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ellie.billiardsgame.FRAME_DURATION_MS
import com.ellie.billiardsgame.RED1
import com.ellie.billiardsgame.RED2
import com.ellie.billiardsgame.WHITE
import com.ellie.billiardsgame.model.Ball
import com.ellie.billiardsgame.model.Boundary
import com.ellie.billiardsgame.model.Point
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {
    private val _curMode = MutableLiveData(GameMode.READY)
    val curMode: LiveData<GameMode> = _curMode

    private val executor = Executors.newFixedThreadPool(3)
    private var isSimulating = false

    private val balls = listOf(Ball(), Ball(), Ball())
    private val homePositions = listOf(Point(), Point(), Point())
    private val ballCollisionManager = BallCollisionManager(balls)

    private val whiteBall: Ball = balls[WHITE]
    private val redBall1: Ball = balls[RED1]
    private val redBall2: Ball = balls[RED2]

    val whiteBallPosition = whiteBall.point
    val redBall1Position = redBall1.point
    val redBall2Position = redBall2.point

    fun setBoundary(top: Int, right: Int, bottom: Int, left: Int) {
        ballCollisionManager.setBoundary(Boundary(Point(left.toFloat(), top.toFloat()), Point(right.toFloat(), bottom.toFloat())))
    }

    fun updateBall(ballId: Int, x: Float, y: Float) {
        balls[ballId].update(Point(x, y))
    }

    fun updateAvailablePosition(ballId: Int, x: Float, y: Float) {
        ballCollisionManager.updateAvailablePoint(ballId, x, y)
    }

    fun changeMode(mode: GameMode) {
        _curMode.postValue(mode)
    }

    fun startSimulation(velocity: Point) {
        initAllBallsVelocity(velocity)
        captureBallPositions()

        executor.submit {
            isSimulating = true
            while(isSimulating) {
                moveBalls()
                Thread.sleep(FRAME_DURATION_MS)
            }
        }
    }

    private fun initAllBallsVelocity(velocity: Point) {
        whiteBall.setVelocity(velocity.x, velocity.y)
        redBall1.setVelocity(0f, 0f)
        redBall2.setVelocity(0f, 0f)
    }

    private fun captureBallPositions() {
        for (i in balls.indices) {
            homePositions[i].update(balls[i].point.value!!)
        }
    }

    private fun moveBalls() {
        for (i in balls.indices) {
            with (balls[i]) {
                decreaseVelocity()
                ballCollisionManager.updateAvailablePoint(i, nextX, nextY)
            }
        }
    }
    fun endSimulationAndRestorePositions() {
        isSimulating = false
        restoreBallPositions()
    }

    private fun restoreBallPositions() {
        for (i in balls.indices) {
            balls[i].update(homePositions[i])
        }
    }
}