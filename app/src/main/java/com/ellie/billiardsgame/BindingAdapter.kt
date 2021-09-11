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

@BindingAdapter("guidelineStart")
fun LineCanvasView.setGuidelineStart(start: Point) {
    drawLine(start = start)
}

@BindingAdapter("guidelineEnd")
fun LineCanvasView.setGuidelineEnd(end: Point) {
    drawLine(end = end)
}