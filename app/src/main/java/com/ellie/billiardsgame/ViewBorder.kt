package com.ellie.billiardsgame

import android.view.View

class ViewBorder(view: View) {
    private val top: Int = view.top
    private val left: Int = view.left
    private val bottom: Int = view.top + view.height
    private val right: Int = view.left + view.width
    
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