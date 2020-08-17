package com.ellie.billiardsgame.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.ui.MainActivity.Companion.FRAME_DURATION_MS

class Ball{
    private val _point = MutableLiveData(Point(0f, 0f))
    val point: LiveData<Point> = _point

    private var dx = DEFAULT_POWER
    private var dy = -DEFAULT_POWER

    val nextX: Float
        get() = _point.value!!.x + dx

    val nextY: Float
        get() = _point.value!!.y + dy

    fun move(x: Float, y: Float) {
        _point.postValue(Point(x, y))
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