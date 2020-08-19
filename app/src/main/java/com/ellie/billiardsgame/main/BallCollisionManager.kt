package com.ellie.billiardsgame.main

import com.ellie.billiardsgame.GlobalApplication
import com.ellie.billiardsgame.model.Ball
import com.ellie.billiardsgame.model.Boundary
import com.ellie.billiardsgame.model.Point
import kotlin.math.hypot

class BallCollisionManager(private val balls: List<Ball>) {
    private var boundary = Boundary()

    fun setBoundary(boundary: Boundary) {
        this.boundary = boundary
    }

    fun updateAvailablePoint(ballId: Int, x: Float, y: Float) {
        val adjustedPointToBound = getAdjustedPointToBound(ballId, x, y)

        if (isAvailable(ballId, adjustedPointToBound)) {
            balls[ballId].update(adjustedPointToBound)
        } else {
            // TODO - 충돌 시뮬레이션
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