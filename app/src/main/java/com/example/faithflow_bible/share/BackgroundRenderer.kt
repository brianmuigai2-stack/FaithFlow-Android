package com.example.faithflow_bible.share

import android.content.Context
import android.graphics.*
import android.graphics.BitmapFactory
import android.util.Log
import com.example.faithflow_bible.R
import kotlin.math.cos
import kotlin.math.sin

object BackgroundRenderer {

    private const val TAG = "BackgroundRenderer"

    fun drawBackground(canvas: Canvas, ctx: Context, width: Int, height: Int) {
        val bg = loadBackgroundBitmap(ctx, width, height)
        if (bg != null) {
            canvas.drawBitmap(bg, 0f, 0f, null)
        } else {
            drawFallbackGradient(canvas, width, height)
        }
    }

    private fun loadBackgroundBitmap(ctx: Context, width: Int, height: Int): Bitmap? {
        return runCatching {
            val raw = ctx.resources.openRawResource(R.drawable.verse_card_bg).use { it.readBytes() }
            val opts = BitmapFactory.Options().apply {
                inSampleSize = 1
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val decoded = BitmapFactory.decodeByteArray(raw, 0, raw.size, opts)
                ?: return null
            Bitmap.createScaledBitmap(decoded, width, height, true).also {
                if (decoded != it) decoded.recycle()
            }
        }.onFailure { Log.w(TAG, "Background load failed: ${it.message}") }.getOrNull()
    }

    private fun drawFallbackGradient(canvas: Canvas, width: Int, height: Int) {
        // Matches web fallback: radial from upper-center fire to dark edges
        val cx = width / 2f
        val cy = height * 0.28f
        val gradient = RadialGradient(
            cx, cy, height * 0.9f,
            intArrayOf(
                Color.parseColor("#fe3b03"),
                Color.parseColor("#8b1a0a"),
                Color.parseColor("#0d1819"),
                Color.parseColor("#060e10")
            ),
            floatArrayOf(0f, 0.25f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply { shader = gradient })
    }

    // Matches web drawLightRays: triangular rays from cy = h*0.28, full 360°, 14 rays
    fun drawLightRays(canvas: Canvas, width: Int, height: Int) {
        val cx = width / 2f
        val cy = height * 0.28f
        val len = height * 0.72f
        val spread = 0.018
        val rays = 14
        val path = Path()
        for (i in 0 until rays) {
            val angle = (i.toDouble() / rays) * Math.PI * 2
            val paint = Paint().apply {
                isAntiAlias = true
                val c0: Int = Color.argb(46, 255, 160, 60)
                val c1: Int = Color.argb(18, 255, 120, 20)
                val c2: Int = Color.argb(0, 255, 80, 0)
                shader = LinearGradient(
                    cx, cy,
                    (cx + cos(angle) * len).toFloat(), (cy + sin(angle) * len).toFloat(),
                    intArrayOf(c0, c1, c2),
                    floatArrayOf(0f, 0.4f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            path.reset()
            path.moveTo(cx, cy)
            path.lineTo(
                (cx + cos(angle - spread) * len).toFloat(),
                (cy + sin(angle - spread) * len).toFloat()
            )
            path.lineTo(
                (cx + cos(angle + spread) * len).toFloat(),
                (cy + sin(angle + spread) * len).toFloat()
            )
            path.close()
            canvas.drawPath(path, paint)
        }

        // Central bright glow over fire source
        val gc0: Int = Color.argb(56, 255, 200, 80)
        val gc1: Int = Color.argb(31, 255, 120, 20)
        val gc2: Int = Color.argb(0, 255, 60, 0)
        val glow = RadialGradient(
            cx, cy, 340f,
            intArrayOf(gc0, gc1, gc2),
            floatArrayOf(0f, 0.3f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply { shader = glow; isAntiAlias = true })

        // Dark vignette: radial from center, matches web exactly
        val vCx = cx
        val vCy = height * 0.5f
        val vc0: Int = Color.argb(89, 0, 0, 0)
        val vc1: Int = Color.argb(209, 0, 0, 0)
        val vignette = RadialGradient(
            vCx, vCy, height * 0.9f,
            intArrayOf(vc0, vc1),
            floatArrayOf(0.15f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply { shader = vignette; isAntiAlias = true })
    }
}
