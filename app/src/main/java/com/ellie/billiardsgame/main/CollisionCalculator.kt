package com.ellie.billiardsgame.main

import com.ellie.billiardsgame.model.Ball
import com.ellie.billiardsgame.model.Point
import kotlin.math.hypot

class CollisionCalculator(private val ballA: Ball, private val ballB: Ball) {
    private val powerA = Point(ballA.dx, ballA.dy)
    private val powerB = Point(ballB.dx, ballB.dy)

    private val centerVector = Point(ballB.x - ballA.x, ballB.y - ballA.y)
    private val normalVector = Point(centerVector.y, -centerVector.x)

    private val centerAProjectionVector = unit(centerVector) * ((powerA * centerVector) / size(centerVector))
    private val centerBProjectionVector = unit(centerVector) * ((powerB * centerVector) / size(centerVector))
    private val normalAProjectionVector = unit(normalVector) * ((powerA * normalVector) / size(normalVector))
    private val normalBProjectionVector = unit(normalVector) * ((powerB * normalVector) / size(normalVector))

    private val velocityA = normalAProjectionVector + centerBProjectionVector
    private val velocityB = normalBProjectionVector + centerAProjectionVector

    fun updateVelocity() {
        ballA.dx = velocityA.x
        ballA.dy = velocityA.y
        ballB.dx = velocityB.x
        ballB.dy = velocityB.y
    }

    private fun size(vector: Point) = hypot(vector.x, vector.y)

    private fun unit(vector: Point): Point {
        val vectorSize = size(vector)

        val x = vector.x / vectorSize
        val y = vector.y / vectorSize

        return Point(x, y)
    }
}