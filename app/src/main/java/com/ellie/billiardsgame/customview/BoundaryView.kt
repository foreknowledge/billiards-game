package com.ellie.billiardsgame.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View

open class BoundaryView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun validX(newX: Float, targetWidth: Float): Float {
        return when {
            (newX <= left) -> left.toFloat()
            (newX >= right - targetWidth) -> right.toFloat() - targetWidth
            else -> newX
        }
    }

    fun validY(newY: Float, targetHeight: Float): Float {
        return when {
            (newY <= top) -> top.toFloat()
            (newY >= bottom - targetHeight) -> bottom.toFloat() - targetHeight
            else -> newY
        }
    }
}