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
    var ballDiameter = 0f

    val whiteBall = Ball()
    private val redBall1 = Ball()
    private val redBall2 = Ball()
    private var boundary = Boundary()

    private val homePositions = arrayListOf(Point(0f, 0f), Point(0f, 0f), Point(0f, 0f))

    private val _curMode = MutableLiveData(BilliardsMode.READY)
    val curMode: LiveData<BilliardsMode> = _curMode

    private val executor = Executors.newFixedThreadPool(3)
    private var isSimulating = false

    fun setWhiteBallPosition(x: Float, y: Float) {
        whiteBall.move(x, y)
    }

    fun setRedBall1Position(x: Float, y: Float) {
        redBall1.move(x, y)
    }

    fun setRedBall2Position(x: Float, y: Float) {
        redBall2.move(x, y)
    }

    fun setBoundary(top: Int, right: Int, bottom: Int, left: Int) {
        boundary = Boundary(Point(left.toFloat(), top.toFloat()), Point(right.toFloat(), bottom.toFloat()))
    }

    fun whiteBallUpdate(x: Float, y: Float) {
        val newX = boundary.adjustX(x, ballDiameter)
        val newY = boundary.adjustY(y, ballDiameter)

        whiteBall.move(newX, newY)
    }

    fun changeMode(mode: BilliardsMode) {
        _curMode.value = mode
    }

    fun startSimulation(velocity: Point) {
        whiteBall.dx = velocity.x
        whiteBall.dy = velocity.y

        isSimulating = true
        captureHomePositions()

        executor.submit {
            while(isSimulating) {
                whiteBallUpdate()
                Thread.sleep(FRAME_DURATION_MS)
            }
        }
    }

    private fun captureHomePositions() {
        homePositions[WHITE] = whiteBall.point.value!!
        homePositions[RED1] = redBall1.point.value!!
        homePositions[RED2] = redBall2.point.value!!
    }

    private fun whiteBallUpdate() {
        whiteBall.decreaseVelocityX()
        whiteBall.decreaseVelocityY()

        val newX = boundary.adjustX(whiteBall.nextX, ballDiameter) { whiteBall.changeDirectionX() }
        val newY = boundary.adjustY(whiteBall.nextY, ballDiameter) { whiteBall.changeDirectionY() }

        whiteBall.move(newX, newY)
    }

    fun stopSimulation() {
        isSimulating = false
        restoreBallPositions()
    }

    private fun restoreBallPositions() {
        whiteBall.move(homePositions[WHITE])
        redBall1.move(homePositions[RED1])
        redBall2.move(homePositions[RED2])
    }

    companion object {
        private const val WHITE = 0
        private const val RED1 = 1
        private const val RED2 = 2
    }
}