package com.ellie.billiardsgame.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ellie.billiardsgame.MAX_GUIDELINE_LENGTH
import com.ellie.billiardsgame.MAX_POWER
import kotlin.math.hypot
import kotlin.math.min
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
    private val dySign get() = if (dy < 0) (-1) else 1

    val velocity: Point get() {
        // 비율 = 현재 길이 / 최대 길이
        val ratio = length / MAX_GUIDELINE_LENGTH

        return if (dx != 0f) {
            // 안내선의 기울기
            val slope = if (dx == 0f) 0f else dy / dx

            // 비율에 따른 x, y 속도 계산
            val velocityX = dxSign * sqrt((MAX_POWER * ratio).pow(2) / (1 + slope.pow(2)))
            val velocityY = slope * velocityX

            Point(velocityX, velocityY)
        } else {
            val velocityY = dySign * MAX_POWER * ratio
            Point(0f, velocityY)
        }
    }

    fun setPoints(start: Point, end: Point) {
        _start.postValue(start)
        _end.postValue(calcEndPoint(start, end))
    }

    /**
     * 최대 길이를 고려해 endPoint를 계산해서 반환한다.
     */
    private fun calcEndPoint(start: Point, end: Point): Point {
        val length = (start - end).size()
        return if (length > MAX_GUIDELINE_LENGTH) {
            val lengthVector = (end - start).normalize() * MAX_GUIDELINE_LENGTH
            start + lengthVector
        } else end
    }

    /**
     * 안내선의 방향을 유지한 채 길이를 변경한다.
     */
    fun setLength(length: Float) {
        if (startPoint == endPoint) return

        val limitedLength = min(length, MAX_GUIDELINE_LENGTH)

        // 안내선을 길만큼 scaling 한다.
        val lengthVector = (endPoint - startPoint).normalize() * limitedLength

        // start point 에서 자른 안내선 길이만큼 더한 end point 를 적용한다.
        _end.postValue(startPoint + lengthVector)
    }
}