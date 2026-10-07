package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.*

object QuoteRenderer {

    // Matches web: opening quote at (margin-22, 185), 240px, rgba(255,160,60,0.12)
    fun drawOpeningQuote(canvas: Canvas, ctx: Context, margin: Float) {
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.12f * 255).toInt(), 255, 160, 60)
            textSize = 240f
            textAlign = Paint.Align.LEFT
            typeface = CardFonts.serif(ctx)
        }
        canvas.drawText("\u201C", margin - 22f, 185f + (-paint.ascent()), paint)
    }

    // Matches web: closing quote at right side, after last line
    fun drawClosingQuote(canvas: Canvas, ctx: Context, margin: Float, verseBottom: Float, lineHeight: Float) {
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.12f * 255).toInt(), 255, 160, 60)
            textSize = 240f
            textAlign = Paint.Align.RIGHT
            typeface = CardFonts.serif(ctx)
        }
        val cardWidth = CardStyle.CARD_WIDTH
        canvas.drawText("\u201D", cardWidth - margin + 22f, verseBottom - lineHeight * 0.55f + (-paint.ascent()), paint)
    }
}
