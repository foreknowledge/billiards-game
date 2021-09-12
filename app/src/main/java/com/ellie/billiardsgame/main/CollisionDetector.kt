package com.ellie.billiardsgame.main

import android.graphics.Rect
import com.ellie.billiardsgame.GlobalApplication
import com.ellie.billiardsgame.model.Point

/**
 * 공의 충돌 여부를 판단한다.
 */
object CollisionDetector {
    /**
     * 경계를 넘어가지 않는 위치를 반환한다.
     */
    fun getAdjustedPointToBound(x: Float, y: Float, boundary: Rect): Point {
        val newX = getAdjustedX(x, boundary)
        val newY = getAdjustedY(y, boundary)

        return Point(newX, newY)
    }

    /**
     * 공 2개가 충돌했는지 여부를 계산한다.
     */
    fun isBallCollided(pointA: Point, pointB: Point) =
        (pointA - pointB).size() < GlobalApplication.ballDiameter

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * [rect]를 넘어가지 않는 x 좌표를 반환한다.
     */
    private fun getAdjustedX(xPos: Float, rect: Rect): Float {
        return when {
            (xPos < rect.left) -> rect.left.toFloat()
            (xPos > rect.right) -> rect.right.toFloat()
            else -> xPos
        }
    }

    /**
     * [rect]를 넘어가지 않는 y 좌표를 반환한다.
     */
    private fun getAdjustedY(yPos: Float, rect: Rect): Float {
        return when {
            (yPos < rect.top) -> rect.top.toFloat()
            (yPos > rect.bottom) -> rect.bottom.toFloat()
            else -> yPos
        }
    }
}