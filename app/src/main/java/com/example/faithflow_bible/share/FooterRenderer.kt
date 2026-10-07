package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.*

object FooterRenderer {

    // Matches web: drawRuleCross at h-162, app URL at h-108
    fun drawFooter(canvas: Canvas, ctx: Context, width: Int, height: Int, deepLink: String) {
        HeaderRenderer.drawRuleCross(canvas, width, height - 162f, 110f, 0.45f)

        val urlPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.38f * 255).toInt(), 255, 240, 220)
            textSize = 26f
            textAlign = Paint.Align.CENTER
            typeface = CardFonts.sans(ctx)
        }
        canvas.drawText(deepLink, width / 2f, height - 108f, urlPaint)
    }
}
