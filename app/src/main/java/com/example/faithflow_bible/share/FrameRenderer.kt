package com.example.faithflow_bible.share

import android.graphics.*

object FrameRenderer {

    // Matches web drawOrnamentalFrame: pad=38, outer rgba(255,180,60,0.3), inner rgba(255,180,60,0.12), diamond corners
    fun drawFrame(canvas: Canvas, width: Int, height: Int) {
        val pad = 38f

        val outerPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = Color.argb((0.3f * 255).toInt(), 255, 180, 60)
        }
        canvas.drawRoundRect(RectF(pad, pad, width - pad, height - pad), 28f, 28f, outerPaint)

        val innerPad = pad + 12f
        val innerPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = Color.argb((0.12f * 255).toInt(), 255, 180, 60)
        }
        canvas.drawRoundRect(RectF(innerPad, innerPad, width - innerPad, height - innerPad), 20f, 20f, innerPaint)

        // Diamond corners
        val diamondPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.6f * 255).toInt(), 255, 180, 60)
        }
        val corners = listOf(
            Pair(pad + 8f, pad + 8f),
            Pair(width - pad - 8f, pad + 8f),
            Pair(pad + 8f, height - pad - 8f),
            Pair(width - pad - 8f, height - pad - 8f)
        )
        for ((cx, cy) in corners) {
            val path = Path().apply {
                moveTo(cx, cy - 5f)
                lineTo(cx + 5f, cy)
                lineTo(cx, cy + 5f)
                lineTo(cx - 5f, cy)
                close()
            }
            canvas.drawPath(path, diamondPaint)
        }
    }
}
