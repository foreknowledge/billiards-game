package com.ellie.billiardsgame.model

import com.ellie.billiardsgame.MAX_GUIDELINE_LENGTH
import com.ellie.billiardsgame.MAX_POWER
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 안내선의 데이터를 관리한다.
 */
class Guideline {

    //----------------------------------------------------------
    // Public interface
    //

    // 안내선의 시작점
    var start = Point()

    // 안내선의 끝점
    var end = Point()

    // 안내선의 점의 위치를 외부에 알려주기 위한 필드
    val points: FloatArray
        get() = floatArrayOf(start.x, start.y, end.x, end.y)

    // 안내선의 길이
    val length
        get() = hypot(dx, dy)

    // 안내선의 x 변량
    val dx
        get() = end.x - start.x

    // 안내선의 y 변량
    val dy
        get() = end.y - start.y

    fun setPoints(startX: Float, startY: Float, endX: Float, endY: Float) {
        start.x = startX
        start.y = startY
        end.x = endX
        end.y = endY
    }

    /**
     * 안내선의 길이와 방향에 따른 속도를 계산해서 반환한다.
     */
    fun getVelocity(): Point {
        // 비율 = 현재 길이 / 최대 길이
        val ratio = length / MAX_GUIDELINE_LENGTH
        // 안내선의 기울기
        val slope = if (dx == 0f) 0f else dy / dx

        // 비율에 따른 x, y 속도 계산
        val velocityX = getSign(dx) * sqrt((MAX_POWER * ratio).pow(2) / (1 + slope.pow(2)))
        val velocityY = slope * velocityX

        return Point(velocityX, velocityY)
    }

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * 안내선의 x 변량이 음수면 -1을 반환하고, 양수면 1을 반환한다.
     */
    private fun getSign(dx: Float) = if (dx < 0) (-1) else 1
}