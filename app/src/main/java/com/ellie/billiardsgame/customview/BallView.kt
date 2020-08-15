package com.ellie.billiardsgame.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ellie.billiardsgame.MissingAttributeException
import com.ellie.billiardsgame.R

@SuppressLint("ClickableViewAccessibility")
class BallView : View {
    private val ballRadius by lazy {
        resources.getDimension(R.dimen.ball_diameter_size) / 2
    }

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
            x = boundaryView.validX(event.rawX - ballRadius, ballRadius * 2)
            y = boundaryView.validY(event.rawY - ballRadius, ballRadius * 2)
        }

        return true
    }
}