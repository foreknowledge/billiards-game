package com.ellie.billiardsgame.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.FRAME_DURATION_MS

/**
 * 공의 위치, 속도를 관리한다.
 */
class Ball {
    // 공의 위치
    private val _point = MutableLiveData(Point())
    val point: LiveData<Point> = _point

    val x get() = point.value!!.x
    val y get() = point.value!!.y

    // 공의 속도
    var dx = 0f
        private set
    var dy = 0f
        private set

    // 다음 위치 = 이전 위치 + 속도
    val nextX: Float get() = x + dx
    val nextY: Float get() = y + dy

    fun update(point: Point) {
        _point.postValue(point)
    }

    fun setVelocity(dx: Float, dy: Float) {
        this.dx = dx
        this.dy = dy
    }

    fun changeDirectionX() { dx = -dx }
    fun changeDirectionY() { dy = -dy }

    /**
     * 마찰력을 적용해서 공의 속도를 감소시킨다.
     */
    fun decreaseVelocity() {
        dx -= FRICTION * dx
        dy -= FRICTION * dy
    }

    //----------------------------------------------------------
    // Constant definitions.
    //

    companion object {
        private const val FRICTION = FRAME_DURATION_MS * 0.0005f
    }
}