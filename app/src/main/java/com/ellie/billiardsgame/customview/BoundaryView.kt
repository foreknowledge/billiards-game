package com.ellie.billiardsgame.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.ellie.billiardsgame.listener.OnDrawLineListener

class BoundaryView : View {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var endX: Float = 0f
    private var endY: Float = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.LTGRAY
        strokeWidth = 10f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val onDrawLineListener = object : OnDrawLineListener {
        override fun onDrawLine(startRawX: Float, startRawY: Float, endRawX: Float, endRawY: Float) {
            startX = startRawX - x
            startY = startRawY - y
            endX = endRawX - x
            endY = endRawY - y

            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(startX, startY, endX, endY, paint)
    }
}