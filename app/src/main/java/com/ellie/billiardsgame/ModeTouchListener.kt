package com.ellie.billiardsgame

import android.view.MotionEvent

interface ModeTouchListener {
    fun onWhiteBallTouch(event: MotionEvent): Boolean
    fun onPoolTableTouch(event: MotionEvent): Boolean
}