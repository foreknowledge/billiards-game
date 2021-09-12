package com.ellie.billiardsgame

import android.view.View
import androidx.databinding.BindingAdapter
import com.ellie.billiardsgame.customview.LineCanvasView
import com.ellie.billiardsgame.model.Point

@BindingAdapter("position")
fun View.setPosition(point: Point) {
    x = point.x
    y = point.y
}

@BindingAdapter("guidelineStart", "guidelineEnd")
fun LineCanvasView.setGuideline(start: Point, end: Point) {
    drawLine(start, end)
}