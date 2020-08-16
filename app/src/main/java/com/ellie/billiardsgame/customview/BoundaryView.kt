package com.ellie.billiardsgame.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

@SuppressLint("ClickableViewAccessibility")
class BoundaryView : View {
    private var isEditMode = false
    private var startView: BallView? = null

    private var endX = 0f
    private var endY = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.LTGRAY
        strokeWidth = 10f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        isEditMode = true

        if (event.action == MotionEvent.ACTION_MOVE) {
            endX = event.x
            endY = event.y
            invalidate()
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (startView != null && isEditMode) {
            val startX = startView!!.centerX - x
            val startY = startView!!.centerY - y

            canvas.drawLine(startX, startY, endX, endY, paint)
        }
    }

    fun setStartView(ballView: BallView) {
        startView = ballView
    }

    fun adjustX(newX: Float, targetWidth: Float, outside: () -> Unit = {}): Float {
        return when {
            (newX < left) -> {
                outside()
                left.toFloat()
            }
            (newX > right - targetWidth) -> {
                outside()
                right.toFloat() - targetWidth
            }
            else -> newX
        }
    }

    fun adjustY(newY: Float, targetHeight: Float, outside: () -> Unit = {}): Float {
        return when {
            (newY < top) -> {
                outside()
                top.toFloat()
            }
            (newY > bottom - targetHeight) -> {
                outside()
                bottom.toFloat() - targetHeight
            }
            else -> newY
        }
    }
}