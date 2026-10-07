package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.*

object MetadataRenderer {

    // Matches web: theme pill, reference at y, translation at y+66
    fun drawMetadata(
        canvas: Canvas,
        ctx: Context,
        theme: String,
        reference: String,
        translation: String,
        margin: Float,
        y: Float
    ): Float {
        var currentY = y

        // Theme pill — matches web exactly
        if (theme.isNotBlank()) {
            val label = "\u2736  ${theme.uppercase()}  \u2736"
            val textPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#ffd080")
                textSize = 30f
                typeface = CardFonts.sans(ctx)
            }
            val pillW = textPaint.measureText(label) + 64f
            val pillH = 52f

            val bgPaint = Paint().apply {
                isAntiAlias = true
                color = Color.argb((0.12f * 255).toInt(), 255, 140, 20)
            }
            canvas.drawRoundRect(RectF(margin, currentY, margin + pillW, currentY + pillH), 26f, 26f, bgPaint)

            val strokePaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = 1f
                color = Color.argb((0.35f * 255).toInt(), 255, 180, 60)
            }
            canvas.drawRoundRect(RectF(margin, currentY, margin + pillW, currentY + pillH), 26f, 26f, strokePaint)

            canvas.drawText(label, margin + 32f, currentY + 13f + (-textPaint.ascent()), textPaint)
            currentY += 80f
        } else {
            currentY += 30f
        }

        // Reference — matches web: "#ffd080", 46px serif bold, shadow
        val refPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#ffd080")
            textSize = 52f
            typeface = CardFonts.serifBold(ctx)
            setShadowLayer(6f, 0f, 2f, Color.argb((0.9f * 255).toInt(), 0, 0, 0))
        }
        canvas.drawText("\u2014 $reference", margin, currentY + (-refPaint.ascent()), refPaint)

        // Translation — matches web: rgba(255,240,220,0.9), 26px sans, at y+66
        val transPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.9f * 255).toInt(), 255, 240, 220)
            textSize = 30f
            typeface = CardFonts.sans(ctx)
            setShadowLayer(4f, 0f, 0f, Color.argb((0.8f * 255).toInt(), 0, 0, 0))
        }
        canvas.drawText(translation, margin, currentY + 66f + (-transPaint.ascent()), transPaint)

        return currentY + 66f + transPaint.descent() - transPaint.ascent()
    }
}
