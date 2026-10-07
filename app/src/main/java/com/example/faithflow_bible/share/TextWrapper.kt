package com.example.faithflow_bible.share

import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint

/**
 * Text wrapping and measurement utilities.
 */
object TextWrapper {

    /**
     * Wraps [text] to fit within [maxWidth] px using [paint].
     * Returns list of line strings.
     */
    fun wrapText(text: String, paint: TextPaint, maxWidth: Float): List<String> {
        if (text.isEmpty()) return emptyList()
        val lines = mutableListOf<String>()
        val paragraphs = text.split("\n")
        for (paragraph in paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add("")
                continue
            }
            wrapParagraph(paragraph, paint, maxWidth, lines)
        }
        return lines
    }

    private fun wrapParagraph(
        paragraph: String,
        paint: TextPaint,
        maxWidth: Float,
        out: MutableList<String>
    ) {
        var start = 0
        while (start < paragraph.length) {
            var lo = start
            var hi = paragraph.length
            var best = start
            while (lo <= hi) {
                val mid = (lo + hi) / 2
                val sub = paragraph.substring(start, mid)
                val width = paint.measureText(sub)
                if (width <= maxWidth) {
                    best = mid
                    lo = mid + 1
                } else {
                    hi = mid - 1
                }
            }
            if (best == start) {
                var breakPoint = start + 1
                while (breakPoint < paragraph.length && !Character.isWhitespace(paragraph[breakPoint])) {
                    breakPoint++
                }
                if (breakPoint > paragraph.length) breakPoint = paragraph.length
                out.add(paragraph.substring(start, breakPoint))
                start = breakPoint
            } else {
                var breakPoint = best
                while (breakPoint > start && !Character.isWhitespace(paragraph[breakPoint - 1])) {
                    breakPoint--
                }
                if (breakPoint == start) {
                    breakPoint = best
                }
                out.add(paragraph.substring(start, breakPoint).trim())
                while (breakPoint < paragraph.length && Character.isWhitespace(paragraph[breakPoint])) {
                    breakPoint++
                }
                start = breakPoint
            }
        }
    }

    fun measureText(text: String, paint: TextPaint): RectF {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val left = -bounds.left.toFloat()
        val right = left + paint.measureText(text)
        val top = -bounds.top.toFloat()
        val bottom = -bounds.bottom.toFloat()
        return RectF(left, top, right, bottom)
    }

    fun computeBlockHeight(
        lines: List<String>,
        paint: TextPaint,
        lineSpacingExtra: Float
    ): Float {
        if (lines.isEmpty()) return 0f
        val ascent = paint.ascent()
        val descent = paint.descent()
        val lineHeight = descent - ascent
        return lineSpacingExtra + lineHeight * lines.size
    }
}