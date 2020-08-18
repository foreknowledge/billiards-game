package com.ellie.billiardsgame.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View

class BallView : View {
    val radius: Float
        get() = width.toFloat() / 2

    val centerX: Float
        get() = x + radius

    val centerY: Float
        get() = y + radius

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}