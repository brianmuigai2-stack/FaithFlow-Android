package com.example.faithflow_bible

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.data.ThemeMode
import com.example.faithflow_bible.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var logoPulse: ObjectAnimator? = null
    private var navigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate. Hands off from the system splash to our own layout.
        installSplashScreen()
        applyTheme()
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStartState()
        binding.root.post { playIntro() }
    }

    /** Honour the saved theme before any view inflates, so the app never flashes the wrong one. */
    private fun applyTheme() {
        val mode = ReaderPrefs(this).themeMode
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    /** Everything starts hidden / offset so the animations have somewhere to travel from. */
    private fun setStartState() = with(binding) {
        val rise = 24f * resources.displayMetrics.density
        splashBackground.scaleX = 1.15f
        splashBackground.scaleY = 1.15f
        splashLogo.alpha = 0f
        splashLogo.scaleX = 0.6f
        splashLogo.scaleY = 0.6f
        splashTitle.alpha = 0f
        splashTitle.translationY = rise
        splashSubtitle.alpha = 0f
        splashSubtitle.translationY = rise
        splashProgress.alpha = 0f
        splashTagline.alpha = 0f
    }

    private fun playIntro() = with(binding) {
        // 1) Background: slow "Ken Burns" zoom-out
        splashBackground.animate()
            .scaleX(1f).scaleY(1f)
            .setDuration(3000)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // 2) Logo: fade in + springy pop
        splashLogo.animate().alpha(1f).setStartDelay(200).setDuration(500).start()
        ObjectAnimator.ofPropertyValuesHolder(
            splashLogo,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.6f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.6f, 1f)
        ).apply {
            startDelay = 200
            duration = 800
            interpolator = OvershootInterpolator(1.6f)
            doOnEnd { startLogoPulse() }
        }.start()

        // 3) Title + subtitle rise into place, staggered
        splashTitle.riseIn(delay = 600)
        splashSubtitle.riseIn(delay = 850)

        // 4) Tagline + progress bar fade in, then the bar fills
        splashTagline.animate().alpha(1f).setStartDelay(1100).setDuration(600).start()
        splashProgress.animate().alpha(1f).setStartDelay(900).setDuration(400).start()

        ValueAnimator.ofInt(0, 100).apply {
            startDelay = 900
            duration = 1800
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { splashProgress.setProgressCompat(it.animatedValue as Int, false) }
            // small beat at 100% so it doesn't feel rushed, then move on
            doOnEnd { binding.root.postDelayed({ goToMain() }, 300) }
        }.start()
    }

    private fun View.riseIn(delay: Long, duration: Long = 600) {
        animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    /** A gentle "breathing" loop on the logo while the bar fills. */
    private fun startLogoPulse() {
        logoPulse = ObjectAnimator.ofPropertyValuesHolder(
            binding.splashLogo,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.06f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.06f)
        ).apply {
            duration = 1100
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun goToMain() {
        if (navigated || isFinishing || isDestroyed) return
        navigated = true
        startActivity(Intent(this, MainActivity::class.java))
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE, android.R.anim.fade_in, android.R.anim.fade_out
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        finish()
    }

    override fun onDestroy() {
        logoPulse?.cancel()
        super.onDestroy()
    }
}
