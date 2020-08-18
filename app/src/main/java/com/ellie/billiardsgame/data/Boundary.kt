package com.ellie.billiardsgame.data

class Boundary(
    private val leftTopPoint: Point = Point(0f, 0f),
    private val rightBottomPoint: Point = Point(0f, 0f)
) {
    fun adjustX(newX: Float, targetWidth: Float, outside: () -> Unit = {}): Float {
        return when {
            (newX < leftTopPoint.x) -> {
                outside()
                leftTopPoint.x
            }
            (newX > rightBottomPoint.x - targetWidth) -> {
                outside()
                rightBottomPoint.x - targetWidth
            }
            else -> newX
        }
    }

    fun adjustY(newY: Float, targetHeight: Float, outside: () -> Unit = {}): Float {
        return when {
            (newY < leftTopPoint.y) -> {
                outside()
                leftTopPoint.y
            }
            (newY > rightBottomPoint.y - targetHeight) -> {
                outside()
                rightBottomPoint.y - targetHeight
            }
            else -> newY
        }
    }
}