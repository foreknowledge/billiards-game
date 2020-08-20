package com.ellie.billiardsgame.main

import com.ellie.billiardsgame.model.Ball
import com.ellie.billiardsgame.model.Point
import kotlin.math.hypot

object CollisionCalculator {
    fun applyCollisionVelocity(ballA: Ball, ballB: Ball) {
        val powerA = Point(ballA.dx, ballA.dy)
        val powerB = Point(ballB.dx, ballB.dy)

        val centerVector = Point(ballB.x - ballA.x, ballB.y - ballA.y)
        val normalVector = Point(centerVector.y, -centerVector.x)

        val centerAProjectionVector = unit(centerVector) * ((powerA * centerVector) / size(centerVector))
        val centerBProjectionVector = unit(centerVector) * ((powerB * centerVector) / size(centerVector))
        val normalAProjectionVector = unit(normalVector) * ((powerA * normalVector) / size(normalVector))
        val normalBProjectionVector = unit(normalVector) * ((powerB * normalVector) / size(normalVector))

        val velocityA = normalAProjectionVector + centerBProjectionVector
        val velocityB = normalBProjectionVector + centerAProjectionVector

        ballA.setVelocity(velocityA.x, velocityA.y)
        ballB.setVelocity(velocityB.x, velocityB.y)
    }

    private fun size(vector: Point) = hypot(vector.x, vector.y)

    private fun unit(vector: Point): Point {
        val vectorSize = size(vector)

        val x = vector.x / vectorSize
        val y = vector.y / vectorSize

        return Point(x, y)
    }
}