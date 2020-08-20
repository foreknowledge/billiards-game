package com.ellie.billiardsgame.main

import com.ellie.billiardsgame.GlobalApplication
import com.ellie.billiardsgame.model.Ball
import com.ellie.billiardsgame.model.Boundary
import com.ellie.billiardsgame.model.Point
import kotlin.math.hypot

/**
 * 공의 충돌 여부를 판단하고 충돌 이후에 공의 위치와 속도를 적절한 값으로 설정한다.
 */
class BallCollisionManager(private val balls: List<Ball>) {

    //----------------------------------------------------------
    // Instance data.
    //

    private var boundary = Boundary()

    //----------------------------------------------------------
    // Public interface.
    //

    /**
     * 공의 경계를 설정한다.
     */
    fun setBoundary(boundary: Boundary) {
        this.boundary = boundary
    }

    /**
     * 충돌을 계산해 공의 위치와 속도를 업데이트한다.
     */
    fun updateBallConsideringTheConflict(ballId: Int, x: Float, y: Float) {
        // 경계를 넘어가지 않는 위치 계산
        val adjustedPointToBound = getAdjustedPointToBound(ballId, x, y)

        // 그 위치가 다른 공이랑 충돌했는지 확인
        val collidedBallId = getCollidedBallId(ballId, adjustedPointToBound)

        if (collidedBallId == -1) {
            // 충돌하지 않았다면 그 위치로 update
            balls[ballId].update(adjustedPointToBound)
        } else {
            // 충돌했다면 위치는 update 하지 않고 속도만 update
            CollisionCalculator.applyCollisionVelocity(balls[ballId], balls[collidedBallId])
        }
    }

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * 공이 경계와 충돌한 경우 공의 방향을 업데이트 한 뒤, 경계를 넘어가지 않는 위치를 반환한다.
     */
    private fun getAdjustedPointToBound(ballId: Int, x: Float, y: Float): Point {
        val targetBall = balls[ballId]

        val newX = boundary.getAdjustedX(x, onCollideBoundary = { targetBall.changeDirectionX() })
        val newY = boundary.getAdjustedY(y, onCollideBoundary = { targetBall.changeDirectionY() })

        return Point(newX, newY)
    }

    /**
     * 충돌한 공의 id를 반환한다.
     */
    private fun getCollidedBallId(ballId: Int, nextPoint: Point): Int {
        // 모든 공을 돌면서 확인
        for (i in balls.indices) {
            if (ballId == i) {
                // 자기 자신이면 Pass
                continue
            }
            if (isCollision(nextPoint, balls[i].point.value!!)) {
                // 충돌했으면 상대 공 id 반환
                return i
            }
        }

        // 충돌한 공 없으면 -1 반환
        return -1
    }

    /**
     * 공 2개가 충돌했는지 여부를 계산한다.
     */
    private fun isCollision(pointA: Point, pointB: Point)
            = hypot(pointA.x - pointB.x, pointA.y - pointB.y) < GlobalApplication.ballDiameter
}