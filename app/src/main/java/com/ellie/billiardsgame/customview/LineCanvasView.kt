package com.ellie.billiardsgame.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ellie.billiardsgame.MAX_GUIDELINE_LENGTH
import com.ellie.billiardsgame.model.Guideline

/**
 * 안내선을 그리기 위한 Custom View
 */
class LineCanvasView : View {

    //----------------------------------------------------------
    // Instance data.
    //

    private val guideline = Guideline()

    // 안내선 그리기 위한 페인트
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 10f)
        strokeWidth = 7f
    }

    //----------------------------------------------------------
    // Public interface.
    //

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 안내선을 그리기 위해 콜백 함수 재정의.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLines(guideline.points, paint)
    }

    /**
     * 안내선을 그린다.
     * 안내선의 절대 좌표를 함수 인자로 받는다.
     */
    fun drawLine(startRawX: Float, startRawY: Float, endRawX: Float, endRawY: Float) {
        paint.color = Color.CYAN
        // 상대좌표로 변환해서 좌표를 설정
        guideline.setPoints(startRawX - x, startRawY - y, endRawX - x, endRawY - y)

        // 안내선 최대 길이만큼만 보여줌
        cutToMaxLength()

        // 다시 그리기. onDraw() 콜백 호출
        invalidate()
    }

    /**
     * 안내선을 지운다.
     */
    fun removeLine() {
        paint.color = Color.TRANSPARENT
        guideline.setPoints(0f, 0f, 0f, 0f)

        // 다시 그리기. onDraw() 콜백 호출
        invalidate()
    }

    /**
     * 안내선의 길이와 방향에 따른 속도를 계산해서 반환한다.
     */
    fun getVelocity() = guideline.getVelocity()

    //----------------------------------------------------------
    // Internal support interface.
    //

    /**
     * 안내선 최대 길이만큼 자른다.
     */
    private fun cutToMaxLength() {
        val length = guideline.length
        if (length > MAX_GUIDELINE_LENGTH) {
            // 안내선 길이가 최대 길이를 넘어간 경우

            // 비율 = (최대 길이) / (현재 안내선 길이)
            val ratio = MAX_GUIDELINE_LENGTH / length

            // 안내선의 x 길이, y 길이를 비율에 맞게 자른다.
            val lengthX = guideline.dx * ratio
            val lengthY = guideline.dy * ratio

            // start point 에서 자른 안내선 길이만큼 더한 end point 를 적용한다.
            guideline.end.x = guideline.start.x + lengthX
            guideline.end.y = guideline.start.y + lengthY
        }
    }
}