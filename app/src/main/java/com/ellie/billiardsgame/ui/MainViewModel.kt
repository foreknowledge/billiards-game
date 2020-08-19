package com.ellie.billiardsgame.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ellie.billiardsgame.BilliardsMode
import com.ellie.billiardsgame.FRAME_DURATION_MS
import com.ellie.billiardsgame.data.Ball
import com.ellie.billiardsgame.data.Boundary
import com.ellie.billiardsgame.data.Point
import java.util.concurrent.Executors

class MainViewModel : ViewModel() {
    val whiteBall = Ball()
    val redBall1 = Ball()
    val redBall2 = Ball()
    private var boundary = Boundary()

    private val homePositions = arrayListOf(Point(), Point(), Point())

    private val _curMode = MutableLiveData(BilliardsMode.READY)
    val curMode: LiveData<BilliardsMode> = _curMode

    private val executor = Executors.newFixedThreadPool(3)
    private var isSimulating = false

    fun setBoundary(top: Int, right: Int, bottom: Int, left: Int) {
        boundary = Boundary(Point(left.toFloat(), top.toFloat()), Point(right.toFloat(), bottom.toFloat()))
    }

    fun updatePositionByApplyingCollision(targetBall: Ball, x: Float, y: Float) {
        val newX = boundary.adjustX(x)
        val newY = boundary.adjustY(y)

        // TODO - apply ball-ball collision (위치 적용 안되게 막기)

        targetBall.update(newX, newY)
    }

    fun changeMode(mode: BilliardsMode) {
        _curMode.value = mode
    }

    fun startSimulation(velocity: Point) {
        whiteBall.dx = velocity.x
        whiteBall.dy = velocity.y

        isSimulating = true
        captureBallPositions()

        executor.submit {
            while(isSimulating) {
                moveWhiteBall()
                Thread.sleep(FRAME_DURATION_MS)
            }
        }
    }

    private fun captureBallPositions() {
        homePositions[WHITE] = whiteBall.point.value!!
        homePositions[RED1] = redBall1.point.value!!
        homePositions[RED2] = redBall2.point.value!!
    }

    private fun moveWhiteBall() {
        whiteBall.decreaseVelocityX()
        whiteBall.decreaseVelocityY()

        val newX = boundary.adjustX(whiteBall.nextX) { whiteBall.changeDirectionX() }
        val newY = boundary.adjustY(whiteBall.nextY) { whiteBall.changeDirectionY() }

        // TODO - apply ball-ball collision (충돌 모션 적용)

        whiteBall.update(newX, newY)
    }

    fun stopSimulation() {
        isSimulating = false
        restoreBallPositions()
    }

    private fun restoreBallPositions() {
        whiteBall.update(homePositions[WHITE])
        redBall1.update(homePositions[RED1])
        redBall2.update(homePositions[RED2])
    }

    companion object {
        private const val WHITE = 0
        private const val RED1 = 1
        private const val RED2 = 2
    }
}