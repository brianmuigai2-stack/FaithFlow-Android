package com.example.faithflow_bible.share

import android.graphics.Color

/**
 * Visual style constants for the verse card renderer.
 *
 * All colors are hardcoded ARGB ints (no resource dependencies) so the
 * renderer stays fully testable and independent of Android resource loading.
 */
object CardStyle {

    // ── Background ──────────────────────────────────────────────────────
    val BACKGROUND = Color.argb(255, 11, 11, 13)          // near-black charcoal
    val BACKGROUND_FALLBACK = Color.argb(255, 14, 12, 12) // dark red fallback

    // ── Fire / warm tones ───────────────────────────────────────────────
    val FIRE_RED = Color.argb(255, 139, 34, 19)            // deep red
    val FIRE_ORANGE = Color.argb(255, 191, 85, 28)         // orange
    val FIRE_GOLD = Color.argb(255, 224, 151, 69)          // warm gold
    val FIRE_AMBER = Color.argb(255, 180, 98, 38)          // amber

    // ── Text ────────────────────────────────────────────────────────────
    val TEXT_WHITE = Color.argb(255, 248, 246, 242)
    val TEXT_OFFWHITE = Color.argb(230, 232, 228, 218)
    val TEXT_GOLD = Color.argb(255, 212, 168, 96)
    val TEXT_GOLD_SOFT = Color.argb(200, 212, 168, 96)

    // ── Panel ───────────────────────────────────────────────────────────
    val PANEL_BLACK = Color.argb(128, 0, 0, 0)            // ~50% black
    val PANEL_BORDER = Color.argb(38, 212, 168, 96)       // very subtle gold

    // ── Opacities (alpha 0-255) ─────────────────────────────────────────
    const val ALPHA_RAY = 38        // ~15%
    const val ALPHA_GLOW = 26       // ~10%
    const val ALPHA_VIGNETTE = 140  // ~55%
    const val ALPHA_CROSS = 38       // ~15%
    const val ALPHA_QUOTE = 26       // ~10%
    const val ALPHA_FRAME = 51       // ~20%
    const val ALPHA_FRAME_INNER = 64 // ~25%
    const val ALPHA_RULE = 77        // ~30%
    const val ALPHA_RULE_FOOTER = 38 // ~15%

    // ── Dimensions ──────────────────────────────────────────────────────
    const val CARD_WIDTH = 1080
    const val CARD_HEIGHT_SINGLE = 1350
    const val TEXT_MAX_WIDTH = 860
    const val TEXT_MAX_WIDTH_COLL = 780

    const val PADDINGHorizontal = 96f
    const val PADDING_TOP = 110f
    const val PADDING_BOTTOM = 96f

    const val PANEL_PADDING = 56f
    const val PANEL_RADIUS = 36f

    const val FRAME_INSET = 56f
    const val FRAME_RADIUS = 58f
    const val FRAME_STROKE = 2f
    const val DIAMOND_SIZE = 14f

    const val RAY_LENGTH = 720f
    const val RAY_WIDTH = 3f

    const val HEADER_RULE_TOP = 184f
    const val WORDMARK_TOP = 240f
    const val DOTS_TOP = 308f
    const val VERSE_AREA_TOP = 372f

    const val FOOTER_TOP_RATIO = 0.87f

    const val QUOTE_FONT_SIZE = 180f
    const val QUOTE_BOTTOM_OFFSET = 12f

    // ── Text sizes (initial, before overflow adjustment) ────────────────
    const val VERSE_SIZE_SHORT = 70f
    const val VERSE_SIZE_MEDIUM = 60f
    const val VERSE_SIZE_LONG = 50f
    const val REFERENCE_SIZE = 48f
    const val TRANSLATION_SIZE = 30f
    const val THEME_PILL_TEXT_SIZE = 26f
    const val WORDMARK_SIZE = 42f
    const val FOOTER_SIZE = 26f
    const val DEEPLINK_SIZE = 24f

    const val VERSE_LENGTH_SHORT = 110
    const val VERSE_LENGTH_MEDIUM = 180

    const val LINE_SPACING_EXTRA = 14f
}
