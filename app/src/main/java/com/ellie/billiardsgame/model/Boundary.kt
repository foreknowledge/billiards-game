package com.ellie.billiardsgame.model

import android.graphics.Rect
import com.ellie.billiardsgame.GlobalApplication

/**
 * 경계를 가지고 당구공의 이동 위치를 제한한다.
 */
class Boundary(private val rect: Rect = Rect()) {

    //----------------------------------------------------------
    // Instance data.
    //

    private val ballDiameter = GlobalApplication.ballDiameter

    //----------------------------------------------------------
    // Public interface.
    //

    /**
     * Boundary에 맞춰진 x 좌표를 반환한다.
     * @param onCollideBoundary 당구대와 충돌했을 때 호출하는 함수.
     */
    fun getAdjustedX(newX: Float, onCollideBoundary: () -> Unit): Float {
        return when {
            (newX < rect.left) -> {
                // x 좌표가 left 보다 왼쪽에 있을 경우 -> 충돌
                onCollideBoundary()

                // 경계의 left x좌표 반환
                rect.left.toFloat()
            }
            (newX > rect.right - ballDiameter) -> {
                // x 좌표가 [right x좌표 - 지름] 보다 오른쪽에 있을 경우 -> 충돌

                onCollideBoundary()

                // 경계의 [right x좌표 - 지름] 반환
                rect.right - ballDiameter
            }
            else -> newX    // 충돌하지 않았다면 x좌표 그대로 반환
        }
    }

    /**
     * Boundary에 맞춰진 y 좌표를 반환한다.
     * @param onCollideBoundary 당구대와 충돌했을 때 호출되는 함수.
     */
    fun getAdjustedY(newY: Float, onCollideBoundary: () -> Unit): Float {
        return when {
            (newY < rect.top) -> {
                // y 좌표가 top 보다 아래쪽에 있을 경우 -> 충돌
                onCollideBoundary()

                // 경계의 top y좌표 반환
                rect.top.toFloat()
            }
            (newY > rect.bottom - ballDiameter) -> {
                // y 좌표가 [bottom y좌표 - 지름] 보다 오른쪽에 있을 경우 -> 충돌
                onCollideBoundary()

                // 경계의 [bottom y좌표 - 지름] 반환
                rect.bottom - ballDiameter
            }
            else -> newY    // 충돌하지 않았다면 y좌표 그대로 반환
        }
    }
}