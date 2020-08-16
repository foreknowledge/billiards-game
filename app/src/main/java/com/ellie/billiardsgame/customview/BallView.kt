package com.ellie.billiardsgame.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ellie.billiardsgame.MainActivity
import com.ellie.billiardsgame.MissingAttributeException
import com.ellie.billiardsgame.R

@SuppressLint("ClickableViewAccessibility")
class BallView : View {
    val centerX: Float
        get() = x + ballRadius

    val centerY: Float
        get() = y + ballRadius

    private var dx: Float = 0f
    private var dy: Float = 0f
    private val ballDiameter = resources.getDimension(R.dimen.ball_diameter_size)
    private val ballRadius = ballDiameter / 2

    private var resourceId = 0
    private lateinit var boundaryView: BoundaryView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        getBoundaryViewResourceId(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getBoundaryViewResourceId(attrs)
    }

    private fun getBoundaryViewResourceId(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BallView)
        resourceId = typedArray.getResourceId(R.styleable.BallView_boundary, 0)
        typedArray.recycle()

        if (resourceId == 0) {
            throw MissingAttributeException("You must supply a boundary attribute.")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        boundaryView = (parent as View).findViewById(resourceId)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            x = boundaryView.adjustX(event.rawX - ballRadius, ballDiameter)
            y = boundaryView.adjustY(event.rawY - ballRadius, ballDiameter)
        }

        return true
    }

    fun setPower(dx: Float, dy: Float) {
        this.dx = dx
        this.dy = dy
    }

    fun move() {
        dx -= FRICTION * dx
        dy -= FRICTION * dy

        x = boundaryView.adjustX(x + dx, ballDiameter) { dx = -dx }
        y = boundaryView.adjustY(y + dy, ballDiameter) { dy = -dy }
    }

    companion object {
        private const val FRICTION = MainActivity.FRAME_DURATION * 0.0004f
        private const val MAX_POWER = MainActivity.FRAME_DURATION * 4f
    }
}