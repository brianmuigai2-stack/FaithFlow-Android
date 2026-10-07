package com.example.faithflow_bible.share

import android.graphics.*

object WatermarkRenderer {

    // Matches web: drawCross(ctx, CARD_W/2, CARD_H*0.52, 560, 0.06)
    fun drawCrossWatermark(canvas: Canvas, width: Int, height: Int) {
        val cx = width / 2f
        val cy = height * 0.52f
        val size = 560f
        val lineW = size * 0.065f

        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = lineW
            strokeCap = Paint.Cap.ROUND
            color = Color.parseColor("#ffd080")
            alpha = (0.06f * 255).toInt()
            maskFilter = BlurMaskFilter(size * 0.15f, BlurMaskFilter.Blur.NORMAL)
        }
        // Vertical bar
        canvas.drawLine(cx, cy - size / 2f, cx, cy + size / 2f, paint)
        // Horizontal bar
        canvas.drawLine(cx - size * 0.36f, cy - size * 0.1f, cx + size * 0.36f, cy - size * 0.1f, paint)
    }
}
