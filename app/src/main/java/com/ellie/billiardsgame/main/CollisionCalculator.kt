package com.ellie.billiardsgame.main

import com.ellie.billiardsgame.model.Ball
import com.ellie.billiardsgame.model.Point
import kotlin.math.hypot

/**
 * 공 끼리의 충돌을 계산한다.
 */
object CollisionCalculator {

    //----------------------------------------------------------
    // Public interface.
    //

    /**
     * 충돌한 공 2개의 이후 속도를 계산해 적용한다.
     */
    fun applyCollisionVelocity(ballA: Ball, ballB: Ball) {
        // A의 힘
        val powerA = Point(ballA.dx, ballA.dy)
        // B의 힘
        val powerB = Point(ballB.dx, ballB.dy)

        // A와 B의 중심을 잇는 벡터
        val centerVector = Point(ballB.x - ballA.x, ballB.y - ballA.y)
        // A와 B의 중심의 법선 벡터
        val normalVector = Point(centerVector.y, -centerVector.x)

        // A를 중심 벡터로 사영시킨 벡터 (A가 B에게 작용하는 힘)
        val centerAProjectionVector = unit(centerVector) * ((powerA * centerVector) / size(centerVector))
        // B를 중심 벡터로 사영시킨 벡터 (B가 A에게 작용하는 힘)
        val centerBProjectionVector = unit(centerVector) * ((powerB * centerVector) / size(centerVector))
        // A를 법선 벡터로 사영시킨 벡터 (A에게 작용하는 힘)
        val normalAProjectionVector = unit(normalVector) * ((powerA * normalVector) / size(normalVector))
        // B를 법선 벡터로 사영시킨 벡터 (B에게 작용하는 힘)
        val normalBProjectionVector = unit(normalVector) * ((powerB * normalVector) / size(normalVector))

        // A의 알짜 힘
        val velocityA = normalAProjectionVector + centerBProjectionVector
        // B의 알짜 힘
        val velocityB = normalBProjectionVector + centerAProjectionVector

        // apply
        ballA.setVelocity(velocityA.x, velocityA.y)
        ballB.setVelocity(velocityB.x, velocityB.y)
    }

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * 벡터의 길이를 반환한다.
     */
    private fun size(vector: Point) = hypot(vector.x, vector.y)

    /**
     * 벡터의 단위 벡터를 반환한다.
     */
    private fun unit(vector: Point): Point {
        val vectorSize = size(vector)

        val x = vector.x / vectorSize
        val y = vector.y / vectorSize

        return Point(x, y)
    }
}