package com.ellie.billiardsgame

import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.databinding.BindingAdapter
import com.ellie.billiardsgame.customview.LineCanvasView
import com.ellie.billiardsgame.model.Point
import kotlin.math.acos
import kotlin.math.hypot

@BindingAdapter("position")
fun View.setPosition(point: Point) {
    x = point.x
    y = point.y
}

@BindingAdapter("guidelineStart", "guidelineEnd")
fun LineCanvasView.setGuideline(start: Point, end: Point) {
    drawLine(start, end)
}

@BindingAdapter("guidelineStart", "guidelineEnd")
fun SeekBar.setPower(start: Point, end: Point) {
    val length = hypot(start.x - end.x, start.y - end.y)
    progress = (length / MAX_GUIDELINE_LENGTH * 100).toInt()
}

@BindingAdapter("startPoint", "endPoint")
fun SeekBar.setDirection(start: Point, end: Point) {
    val v1 = Point(0f, -1f)
    val v2 = end - start
    var theta = acos((v1 * v2) / (v1.size() * v2.size()))

    if (v2.x < 0)
        theta = (2 * Math.PI).toFloat() - theta

    progress = (theta * 100 / (2 * Math.PI)).toInt()
}