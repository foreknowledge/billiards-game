package com.ellie.billiardsgame.data

import com.ellie.billiardsgame.GlobalApplication

class Boundary(
    private val leftTopPoint: Point? = null,
    private val rightBottomPoint: Point? = null
) {
    private val ballDiameter = GlobalApplication.ballDiameter

    fun adjustX(newX: Float, outside: () -> Unit = {}): Float {
        if (isNotInitialized()) {
            return newX
        }

        return when {
            (newX < leftTopPoint!!.x) -> {
                outside()
                leftTopPoint.x
            }
            (newX > rightBottomPoint!!.x - ballDiameter) -> {
                outside()
                rightBottomPoint.x - ballDiameter
            }
            else -> newX
        }
    }

    fun adjustY(newY: Float, outside: () -> Unit = {}): Float {
        if (isNotInitialized()) {
            return newY
        }

        return when {
            (newY < leftTopPoint!!.y) -> {
                outside()
                leftTopPoint.y
            }
            (newY > rightBottomPoint!!.y - ballDiameter) -> {
                outside()
                rightBottomPoint.y - ballDiameter
            }
            else -> newY
        }
    }

    private fun isNotInitialized() = leftTopPoint == null || rightBottomPoint == null
}