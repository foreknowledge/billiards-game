package com.ellie.billiardsgame

import android.view.View
import androidx.databinding.BindingAdapter
import com.ellie.billiardsgame.model.Point

@BindingAdapter("position")
fun View.setPosition(point: Point) {
    x = point.x
    y = point.y
}