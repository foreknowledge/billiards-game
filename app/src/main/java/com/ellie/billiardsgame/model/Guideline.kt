package com.ellie.billiardsgame.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.MAX_GUIDELINE_LENGTH
import com.ellie.billiardsgame.MAX_POWER
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 안내선의 데이터를 관리한다.
 */
class Guideline {
    private val _start = MutableLiveData(Point())
    val start: LiveData<Point> = _start

    private val _end = MutableLiveData(Point())
    val end: LiveData<Point> = _end

    private val startPoint get() = start.value!!
    private val endPoint get() = end.value!!

    private val length get() = hypot(dx, dy)

    private val dx get() = endPoint.x - startPoint.x
    private val dy get() = endPoint.y - startPoint.y

    private val dxSign get() = if (dx < 0) (-1) else 1

    val velocity: Point get() {
        // 비율 = 현재 길이 / 최대 길이
        val ratio = length / MAX_GUIDELINE_LENGTH
        // 안내선의 기울기
        val slope = if (dx == 0f) 0f else dy / dx

        // 비율에 따른 x, y 속도 계산
        val velocityX = dxSign * sqrt((MAX_POWER * ratio).pow(2) / (1 + slope.pow(2)))
        val velocityY = slope * velocityX

        return Point(velocityX, velocityY)
    }

    fun setPoints(start: Point, end: Point) {
        _start.postValue(start)
        _end.postValue(calcMaxEndPoint(start, end))
    }

    /**
     * 최대 길이 만큼 자른 endPoint를 계산해서 반환한다.
     *  * 최대 길이를 넘어 간 경우, 최대 길이만큼 자른 endPoint 반환.
     *  * 최대 길이를 넘어 가지 않은 경우, 받은 endPoint 그대로 반환.
     */
    private fun calcMaxEndPoint(start: Point, end: Point): Point {
        val length = (start - end).size()

        return if (length > MAX_GUIDELINE_LENGTH) {
            // 비율 = (최대 길이) / (현재 안내선 길이)
            val ratio = MAX_GUIDELINE_LENGTH / length

            // 안내선의 x 길이, y 길이를 비율에 맞게 자른다.
            val lengthX = (end.x - startPoint.x) * ratio
            val lengthY = (end.y - startPoint.y) * ratio

            // start point 에서 자른 안내선 길이만큼 더한 end point 를 적용한다.
            Point(startPoint.x + lengthX, startPoint.y + lengthY)
        } else end
    }
}