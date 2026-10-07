package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.*
import android.text.TextPaint

object VerseTextRenderer {

    data class VerseLayout(
        val lines: List<String>,
        val fontSize: Float,
        val lineHeight: Float
    )

    // Matches web: fontSize based on text length, lineHeight = fontSize * 1.44
    fun computeLayout(verseText: String, ctx: Context, maxWidth: Float): VerseLayout {
        val fontSize = when {
            verseText.length > 180 -> 50f
            verseText.length > 110 -> 60f
            else -> 70f
        }
        val paint = makeVersePaint(ctx, fontSize)
        val lines = TextWrapper.wrapText(verseText, paint, maxWidth)
        return VerseLayout(lines, fontSize, fontSize * 1.44f)
    }

    // Matches web: dark panel rgba(0,0,0,0.45), roundRect r=16, text white with shadow
    fun drawVerse(
        canvas: Canvas,
        ctx: Context,
        layout: VerseLayout,
        margin: Float,
        startY: Float
    ): Float {
        val contentW = CardStyle.CARD_WIDTH - margin * 2
        val panelPad = 32f
        val panelH = layout.lines.size * layout.lineHeight + panelPad * 2

        val panelPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.45f * 255).toInt(), 0, 0, 0)
        }
        canvas.drawRoundRect(
            RectF(margin - panelPad, startY - panelPad, margin + contentW + panelPad, startY - panelPad + panelH),
            16f, 16f, panelPaint
        )

        val textPaint = makeVersePaint(ctx, layout.fontSize).apply {
            color = Color.WHITE
            setShadowLayer(8f, 0f, 2f, Color.argb((0.95f * 255).toInt(), 0, 0, 0))
        }

        var y = startY
        for (line in layout.lines) {
            canvas.drawText(line, margin, y + (-textPaint.ascent()), textPaint)
            y += layout.lineHeight
        }

        return y  // bottom of last line (before ascent offset)
    }

    private fun makeVersePaint(ctx: Context, size: Float): TextPaint {
        return TextPaint().apply {
            isAntiAlias = true
            typeface = CardFonts.serifItalic(ctx)
            textSize = size
            textAlign = Paint.Align.LEFT
        }
    }
}
