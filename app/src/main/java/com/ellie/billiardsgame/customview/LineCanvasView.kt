package com.ellie.billiardsgame.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ellie.billiardsgame.MAX_LINE_LENGTH
import com.ellie.billiardsgame.model.GuideLine

class LineCanvasView : View {
    private val line = GuideLine()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 10f)
        strokeWidth = 7f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLines(line.points, paint)
    }

    fun drawLine(startRawX: Float, startRawY: Float, endRawX: Float, endRawY: Float) {
        paint.color = Color.CYAN
        line.setPoints(startRawX - x, startRawY - y, endRawX - x, endRawY - y)

        cutToMaxLength()

        invalidate()
    }

    private fun cutToMaxLength() {
        val distance = line.length
        if (distance > MAX_LINE_LENGTH) {
            val ratio = MAX_LINE_LENGTH / distance
            val distanceX = line.dx * ratio
            val distanceY = line.dy * ratio

            line.end.x = line.start.x + distanceX
            line.end.y = line.start.y + distanceY
        }
    }

    fun removeLine() {
        paint.color = Color.TRANSPARENT
        line.setPoints(0f, 0f, 0f, 0f)

        invalidate()
    }

    fun getVelocity() = line.getVelocity()
}