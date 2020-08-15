package com.ellie.billiardsgame.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View

open class BoundaryView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

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