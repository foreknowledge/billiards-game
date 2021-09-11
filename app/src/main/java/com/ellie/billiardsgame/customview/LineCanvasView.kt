package com.ellie.billiardsgame.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ellie.billiardsgame.model.Point

/**
 * 안내선을 그리기 위한 Custom View
 */
class LineCanvasView : View {

    //----------------------------------------------------------
    // Instance data.
    //

    private var startPoint = Point()
    private var endPoint = Point()

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val points = floatArrayOf(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
        canvas.drawLines(points, paint)
    }

    fun drawLine(start: Point = startPoint, end: Point = endPoint) {
        paint.color = Color.CYAN
        // 상대좌표로 변환해서 좌표를 설정
        startPoint = start
        endPoint = end

        // 다시 그리기. onDraw() 콜백 호출
        invalidate()
    }

    fun removeLine() {
        paint.color = Color.TRANSPARENT
        startPoint = Point()
        endPoint = Point()

        // 다시 그리기. onDraw() 콜백 호출
        invalidate()
    }
}