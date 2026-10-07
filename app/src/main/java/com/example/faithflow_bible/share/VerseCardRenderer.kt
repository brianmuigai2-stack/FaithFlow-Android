package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.*
import com.example.faithflow_bible.data.BibleVerse

class VerseCardRenderer(private val ctx: Context) {

    private val cardW = CardStyle.CARD_WIDTH
    private val cardH = CardStyle.CARD_HEIGHT_SINGLE
    private val margin = 110f
    private val contentW = cardW - margin * 2

    suspend fun generateVerseCard(verse: BibleVerse): Bitmap {
        return runCatching { renderSingle(verse) }
            .onFailure { android.util.Log.e("VerseCardRenderer", "Render failed", it) }
            .getOrNull() ?: renderFallback(verse)
    }

    suspend fun generateCollectionCard(verses: List<BibleVerse>): Bitmap {
        return runCatching { renderCollection(verses) }
            .onFailure { android.util.Log.e("VerseCardRenderer", "Collection render failed", it) }
            .getOrNull() ?: renderFallback(verses.firstOrNull() ?: return renderFallback(null))
    }

    // Matches web drawContent exactly
    private fun renderSingle(verse: BibleVerse): Bitmap {
        val bitmap = Bitmap.createBitmap(cardW, cardH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Background image or fallback
        BackgroundRenderer.drawBackground(canvas, ctx, cardW, cardH)

        // 2. Light rays + glow + vignette (all in drawLightRays to match web)
        BackgroundRenderer.drawLightRays(canvas, cardW, cardH)

        // 3. Ornamental frame
        FrameRenderer.drawFrame(canvas, cardW, cardH)

        // 4. Large faint cross watermark at (w/2, h*0.52), size 560, alpha 0.06
        WatermarkRenderer.drawCrossWatermark(canvas, cardW, cardH)

        // 5. Header: rule+cross at y=108, wordmark at y=146, dots at y=198
        HeaderRenderer.drawHeader(canvas, ctx, cardW)

        // 6. Opening quote watermark
        QuoteRenderer.drawOpeningQuote(canvas, ctx, margin)

        // 7. Verse text panel starting at y=318 (matches web y=318)
        val verseStartY = 318f
        val layout = VerseTextRenderer.computeLayout(verse.text, ctx, contentW)
        val verseBottomY = VerseTextRenderer.drawVerse(canvas, ctx, layout, margin, verseStartY)

        // 8. Closing quote
        QuoteRenderer.drawClosingQuote(canvas, ctx, margin, verseBottomY, layout.lineHeight)

        // 9. Metadata: theme pill, reference, translation (y = verseBottomY + 20)
        MetadataRenderer.drawMetadata(
            canvas, ctx,
            verse.theme, verse.reference,
            "${verse.translation} (${verse.abbreviation})",
            margin, verseBottomY + 20f
        )

        // 10. Footer: rule at h-162, URL at h-108
        val deepLink = buildDeepLink(verse)
        FooterRenderer.drawFooter(canvas, ctx, cardW, cardH, deepLink)

        return bitmap
    }

    // Matches web drawCollectionCard
    private fun renderCollection(verses: List<BibleVerse>): Bitmap {
        val refGap = 20f
        val refH = 50f
        val betweenGap = 56f
        val headerH = 280f
        val footerH = 260f

        // Pre-compute layouts to determine total height
        val tempBitmap = Bitmap.createBitmap(cardW, cardH, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)
        val blocks = verses.map { v ->
            val layout = VerseTextRenderer.computeLayout(v.text, ctx, contentW)
            Triple(v, layout, layout.lines.size * layout.lineHeight)
        }

        val bodyH = blocks.foldIndexed(0f) { i, sum, (_, _, textH) ->
            sum + textH + refGap + refH + if (i < blocks.size - 1) betweenGap else 0f
        }
        val totalH = (headerH + bodyH + footerH).toInt().coerceAtLeast(cardH)

        val bitmap = Bitmap.createBitmap(cardW, totalH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        BackgroundRenderer.drawBackground(canvas, ctx, cardW, totalH)
        BackgroundRenderer.drawLightRays(canvas, cardW, totalH)
        FrameRenderer.drawFrame(canvas, cardW, totalH)

        // Header
        HeaderRenderer.drawRuleCross(canvas, cardW, 108f, margin, 0.55f)
        val wordmarkPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.92f * 255).toInt(), 255, 220, 120)
            textSize = 42f
            textAlign = Paint.Align.CENTER
            typeface = CardFonts.sansMedium(ctx)
        }
        canvas.drawText("F A I T H F L O W", cardW / 2f, 146f + (-wordmarkPaint.ascent()) / 2f, wordmarkPaint)

        val countPaint = Paint().apply {
            isAntiAlias = true
            color = Color.argb((0.65f * 255).toInt(), 255, 240, 220)
            textSize = 32f
            textAlign = Paint.Align.CENTER
            typeface = CardFonts.sansMedium(ctx)
        }
        canvas.drawText("MY VERSES  \u00B7  ${verses.size}", cardW / 2f, 196f + (-countPaint.ascent()) / 2f, countPaint)

        val dotPaint = Paint().apply { isAntiAlias = true; color = Color.argb((0.5f * 255).toInt(), 255, 180, 60) }
        for (i in -3..3) canvas.drawCircle(cardW / 2f + i * 22f, 244f, if (i == 0) 4f else 2.5f, dotPaint)

        // Verses
        var y = headerH
        blocks.forEachIndexed { i, (v, layout, textH) ->
            val panelPad = 28f
            val panelH = textH + panelPad * 2
            val panelPaint = Paint().apply { isAntiAlias = true; color = Color.argb((0.42f * 255).toInt(), 0, 0, 0) }
            canvas.drawRoundRect(RectF(margin - panelPad, y - panelPad, margin + contentW + panelPad, y - panelPad + panelH), 14f, 14f, panelPaint)

            val textPaint = Paint().apply {
                isAntiAlias = true
                color = Color.WHITE
                textSize = layout.fontSize
                typeface = CardFonts.serifItalic(ctx)
                setShadowLayer(8f, 0f, 2f, Color.argb((0.95f * 255).toInt(), 0, 0, 0))
            }
            var ty = y
            for (line in layout.lines) {
                canvas.drawText(line, margin, ty + (-textPaint.ascent()), textPaint)
                ty += layout.lineHeight
            }

            val refPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#ffd080")
                textSize = 42f
                typeface = CardFonts.serifBold(ctx)
                setShadowLayer(6f, 0f, 2f, Color.argb((0.9f * 255).toInt(), 0, 0, 0))
            }
            canvas.drawText("\u2014 ${v.reference}", margin, ty + refGap + (-refPaint.ascent()), refPaint)

            if (i < blocks.size - 1) {
                val divY = ty + refGap + refH
                val divPaint = Paint().apply { isAntiAlias = true; style = Paint.Style.STROKE; strokeWidth = 1.5f; color = Color.argb((0.22f * 255).toInt(), 255, 180, 60) }
                canvas.drawLine(margin, divY, cardW / 2f - 24f, divY, divPaint)
                canvas.drawLine(cardW / 2f + 24f, divY, cardW - margin, divY, divPaint)
                HeaderRenderer.drawRuleCross(canvas, cardW, divY, margin, 0.45f)
                y = divY + betweenGap
            } else {
                y = ty + refGap + refH
            }
        }

        // Footer
        val footerY = totalH - footerH
        HeaderRenderer.drawRuleCross(canvas, cardW, footerY + 20f, margin, 0.4f)
        val ftPaint = Paint().apply {
            isAntiAlias = true; color = Color.parseColor("#ffd080"); textSize = 42f
            textAlign = Paint.Align.CENTER; typeface = CardFonts.serifBold(ctx)
            setShadowLayer(12f, 0f, 0f, Color.argb((0.5f * 255).toInt(), 255, 120, 0))
        }
        canvas.drawText("FaithFlow", cardW / 2f, footerY + 58f + (-ftPaint.ascent()), ftPaint)
        val transPaint = Paint().apply {
            isAntiAlias = true; color = Color.argb((0.5f * 255).toInt(), 255, 240, 220)
            textSize = 30f; textAlign = Paint.Align.CENTER; typeface = CardFonts.sans(ctx)
        }
        canvas.drawText(verses.first().translation, cardW / 2f, footerY + 112f, transPaint)
        val urlPaint = Paint().apply {
            isAntiAlias = true; color = Color.argb((0.32f * 255).toInt(), 255, 240, 220)
            textSize = 26f; textAlign = Paint.Align.CENTER; typeface = CardFonts.sans(ctx)
        }
        canvas.drawText(buildDeepLink(null), cardW / 2f, footerY + 158f, urlPaint)

        return bitmap
    }

    private fun renderFallback(verse: BibleVerse?): Bitmap {
        val bitmap = Bitmap.createBitmap(cardW, cardH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawRect(0f, 0f, cardW.toFloat(), cardH.toFloat(), Paint().apply { color = Color.parseColor("#060e10") })
        if (verse != null) {
            canvas.drawText(verse.reference, cardW / 2f, cardH / 2f, Paint().apply {
                color = Color.WHITE; isAntiAlias = true; textSize = 48f; textAlign = Paint.Align.CENTER
            })
        }
        return bitmap
    }

    fun buildDeepLink(verse: BibleVerse?): String {
        if (verse == null) return "faithflow.app"
        return "faithflow.app/bible?book=${verse.book}&chapter=${verse.chapter}&verse=${verse.verse}"
    }
}
