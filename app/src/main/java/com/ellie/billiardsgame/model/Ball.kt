package com.ellie.billiardsgame.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.FRAME_DURATION_MS

/**
 * 공의 위치, 속도를 관리한다.
 */
class Ball {

    //----------------------------------------------------------
    // Instance data.
    //

    // 공의 위치를 내부에서 변경하기 위한 변수
    private val _point = MutableLiveData(Point())

    //----------------------------------------------------------
    // Public interface.
    //

    // 공의 위치가 변경되었을 때 외부에서 관찰(Observing)하기 위한 변수
    val point: LiveData<Point> = _point

    // 공의 위치의 x 좌표
    val x
        get() = point.value!!.x

    // 공의 위치의 y 좌표
    val y
        get() = point.value!!.y

    // 공의 속도
    var dx = 0f
    var dy = 0f

    // 다음 위치 = 이전 위치 + 속도
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

    /**
     * 공의 x축 이동 방향을 바꾼다.
     */
    fun changeDirectionX() {
        dx = -dx
    }

    /**
     * 공의 y축 이동 방향을 바꾼다.
     */
    fun changeDirectionY() {
        dy = -dy
    }

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