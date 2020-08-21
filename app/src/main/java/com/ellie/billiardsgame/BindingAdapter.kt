package com.ellie.billiardsgame

import androidx.databinding.BindingAdapter
import com.ellie.billiardsgame.customview.BallView
import com.ellie.billiardsgame.model.Point

@BindingAdapter("position")
fun BallView.setPosition(point: Point) {
    x = point.x
    y = point.y
}