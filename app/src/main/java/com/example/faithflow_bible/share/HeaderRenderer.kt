package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.*

object HeaderRenderer {

    // Matches web: drawRuleCross at y=108, wordmark at y=146, dots at y=198
    fun drawHeader(canvas: Canvas, ctx: Context, width: Int) {
        drawRuleCross(canvas, width, 108f, 110f, 0.55f)
        drawWordmark(canvas, ctx, width)
        drawDots(canvas, width)
    }

    // Shared by header and footer: matches web drawRuleCross exactly
    fun drawRuleCross(canvas: Canvas, width: Int, y: Float, margin: Float, alpha: Float) {
        val cx = width / 2f
        val a = (alpha * 255).toInt()
        val linePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = Color.argb(a, 255, 180, 60)
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawLine(margin, y, cx - 34f, y, linePaint)
        canvas.drawLine(cx + 34f, y, width - margin, y, linePaint)
        drawCross(canvas, cx, y, 48f, alpha + 0.1f)
    }

    private fun drawCross(canvas: Canvas, cx: Float, cy: Float, size: Float, alpha: Float) {
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = (size * 0.065f).coerceAtLeast(2f)
            strokeCap = Paint.Cap.ROUND
            color = Color.parseColor("#ffd080")
            this.alpha = (alpha.coerceIn(0f, 1f) * 255).toInt()
            maskFilter = BlurMaskFilter(size * 0.15f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawLine(cx, cy - size / 2f, cx, cy + size / 2f, paint)
        canvas.drawLine(cx - size * 0.36f, cy - size * 0.1f, cx + size * 0.36f, cy - size * 0.1f, paint)
    }

    private fun drawWordmark(canvas: Canvas, ctx: Context, width: Int) {
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.92f * 255).toInt(), 255, 220, 120)
            textSize = 42f
            textAlign = Paint.Align.CENTER
            typeface = CardFonts.sansMedium(ctx)
            maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawText("F A I T H F L O W", width / 2f, 146f + (paint.descent() - paint.ascent()) / 2f, paint)
        paint.maskFilter = null
        canvas.drawText("F A I T H F L O W", width / 2f, 146f + (paint.descent() - paint.ascent()) / 2f, paint)
    }

    private fun drawDots(canvas: Canvas, width: Int) {
        val cx = width / 2f
        val y = 198f
        val dotPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.5f * 255).toInt(), 255, 180, 60)
        }
        for (i in -3..3) {
            val r = if (i == 0) 4f else 2.5f
            canvas.drawCircle(cx + i * 22f, y, r, dotPaint)
        }
    }
}
