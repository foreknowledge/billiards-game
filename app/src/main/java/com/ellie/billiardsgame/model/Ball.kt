package com.ellie.billiardsgame.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.FRAME_DURATION_MS

class Ball {
    private val _point = MutableLiveData(Point())
    val point: LiveData<Point> = _point

    val x
        get() = point.value!!.x

    val y
        get() = point.value!!.y

    var dx = 0f
    var dy = 0f

    val nextX: Float
        get() = x + dx

    val nextY: Float
        get() = y + dy

    fun update(point: Point) {
        _point.postValue(point)
    }

    fun setVelocity(dx: Float, dy: Float) {
        this.dx = dx
        this.dy = dy
    }

    fun changeDirectionX() {
        dx = -dx
    }

    fun changeDirectionY() {
        dy = -dy
    }

    fun decreaseVelocity() {
        dx -= FRICTION * dx
        dy -= FRICTION * dy
    }

    companion object {
        private const val FRICTION = FRAME_DURATION_MS * 0.0005f
    }
}