package com.ellie.billiardsgame.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ellie.billiardsgame.data.Point

class BoundaryView : View {
    private var start = Point(0f, 0f)
    private var end = Point(0f, 0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 10f)
        strokeWidth = 7f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun drawLine(startRawX: Float, startRawY: Float, endRawX: Float, endRawY: Float) {
        paint.color = Color.LTGRAY

        start = Point(startRawX - x, startRawY - y)
        end = Point(endRawX - x, endRawY - y)

        invalidate()
    }

    fun removeLine() {
        paint.color = Color.TRANSPARENT

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }
}