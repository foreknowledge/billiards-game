package com.ellie.billiardsgame.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.FRAME_DURATION_MS

class Ball{
    private val _point = MutableLiveData(Point(0f, 0f))
    val point: LiveData<Point> = _point

    private val x
        get() = point.value!!.x

    private val y
        get() = point.value!!.y

    var dx = DEFAULT_POWER
    var dy = -DEFAULT_POWER

    val nextX: Float
        get() = x + dx

    val nextY: Float
        get() = y + dy

    fun update(x: Float, y: Float) {
        _point.postValue(Point(x, y))
    }

    fun update(point: Point) {
        _point.postValue(point)
    }

    fun changeDirectionX() {
        dx = -dx
    }

    fun changeDirectionY() {
        dy = -dy
    }

    fun decreaseVelocityX() {
        dx -= FRICTION * dx
    }

    fun decreaseVelocityY() {
        dy -= FRICTION * dy
    }

    companion object {
        private const val FRICTION = FRAME_DURATION_MS * 0.0004f
        private const val DEFAULT_POWER = 20f
    }
}