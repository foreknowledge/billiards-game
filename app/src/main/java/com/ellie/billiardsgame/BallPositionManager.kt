package com.ellie.billiardsgame

import com.ellie.billiardsgame.data.Ball
import com.ellie.billiardsgame.data.Boundary
import com.ellie.billiardsgame.data.Point
import kotlin.math.hypot

class BallPositionManager(private val balls: List<Ball>) {
    private var boundary = Boundary()

    fun setBoundary(boundary: Boundary) {
        this.boundary = boundary
    }

    fun updateAvailablePoint(ballId: Int, x: Float, y: Float) {
        val adjustedPointToBound = getAdjustedPointToBound(ballId, x, y)

        if (isAvailable(ballId, adjustedPointToBound)) {
            balls[ballId].update(adjustedPointToBound)
        }
    }

    private fun getAdjustedPointToBound(ballId: Int, x: Float, y: Float): Point {
        val targetBall = balls[ballId]

        val newX = boundary.getAdjustedX(x, collision = { targetBall.changeDirectionX() })
        val newY = boundary.getAdjustedY(y, collision = { targetBall.changeDirectionY() })

        return Point(newX, newY)
    }

    private fun isAvailable(ballId: Int, target: Point): Boolean {
        for (i in balls.indices) {
            if (ballId == i) {
                continue
            }
            if (isCollision(target, balls[i].point.value!!)) {
                return false
            }
        }

        return true
    }

    private fun isCollision(pointA: Point, pointB: Point)
            = hypot(pointA.x - pointB.x, pointA.y - pointB.y) < GlobalApplication.ballDiameter
}