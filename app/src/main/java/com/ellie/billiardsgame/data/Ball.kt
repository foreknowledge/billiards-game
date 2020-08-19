package com.ellie.billiardsgame.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.FRAME_DURATION_MS

class Ball {
    private val _point = MutableLiveData(Point())
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

    fun update(point: Point) {
        _point.postValue(point)
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
        private const val DEFAULT_POWER = 20f
    }
}