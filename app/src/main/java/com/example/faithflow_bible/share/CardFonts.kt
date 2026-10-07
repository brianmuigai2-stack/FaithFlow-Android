package com.example.faithflow_bible.share

import android.graphics.Typeface
import com.example.faithflow_bible.R

/**
 * Font manager for the verse card renderer.
 *
 * Loads bundled fonts from res/font/ with graceful fallback to system fonts.
 * Never throws — always returns a valid Typeface.
 */
object CardFonts {

    // Lazily resolved typefaces
    private var cachedSerif: Typeface? = null
    private var cachedSerifItalic: Typeface? = null
    private var cachedSans: Typeface? = null
    private var cachedSansMedium: Typeface? = null
    private var cachedSerifBold: Typeface? = null

    /** Elegant serif for Scripture text (Playfair Display if available). */
    fun serif(context: android.content.Context): Typeface =
        cachedSerif ?: resolveSerif(context).also { cachedSerif = it }

    /** Italic serif for Scripture emphasis. */
    fun serifItalic(context: android.content.Context): Typeface =
        cachedSerifItalic ?: resolveSerifItalic(context).also { cachedSerifItalic = it }

    /** Clean sans-serif for UI text (Inter if available). */
    fun sans(context: android.content.Context): Typeface =
        cachedSans ?: resolveSans(context).also { cachedSans = it }

    /** Medium weight sans-serif for wordmark. */
    fun sansMedium(context: android.content.Context): Typeface =
        cachedSansMedium ?: resolveSansMedium(context).also { cachedSansMedium = it }

    /** Bold serif for references. */
    fun serifBold(context: android.content.Context): Typeface =
        cachedSerifBold ?: resolveSerifBold(context).also { cachedSerifBold = it }

    private fun resolveSerif(ctx: android.content.Context): Typeface {
        runCatching {
            ctx.resources.getFont(R.font.playfair_display)
        }.onSuccess { return it }
        return Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    }

    private fun resolveSerifItalic(ctx: android.content.Context): Typeface {
        runCatching {
            ctx.resources.getFont(R.font.playfair_display)
        }.onSuccess { base ->
            return Typeface.create(base, Typeface.ITALIC)
        }
        return Typeface.create(Typeface.SERIF, Typeface.ITALIC)
    }

    private fun resolveSans(ctx: android.content.Context): Typeface {
        runCatching {
            ctx.resources.getFont(R.font.inter)
        }.onSuccess { return it }
        return Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private fun resolveSansMedium(ctx: android.content.Context): Typeface {
        runCatching {
            ctx.resources.getFont(R.font.inter_medium)
        }.onSuccess { return it }
        return Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private fun resolveSerifBold(ctx: android.content.Context): Typeface {
        runCatching {
            ctx.resources.getFont(R.font.playfair_display_bold)
        }.onSuccess { return it }
        return Typeface.create(Typeface.SERIF, Typeface.BOLD)
    }

    /** Reset cached typefaces (useful for tests). */
    fun reset() {
        cachedSerif = null
        cachedSerifItalic = null
        cachedSans = null
        cachedSansMedium = null
        cachedSerifBold = null
    }
}
