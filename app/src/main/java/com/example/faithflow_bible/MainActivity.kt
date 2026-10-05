package com.example.faithflow_bible

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.faithflow_bible.databinding.ActivityMainBinding
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.notification.VerseBootReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keep content clear of the status bar, cutouts and the keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            // The bottom bar handles the nav-bar inset itself; when it's hidden, we do.
            val bottom = if (binding.bottomNav.isVisible) ime.bottom else maxOf(ime.bottom, bars.bottom)
            v.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bottom)
            insets
        }

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)

        // The note editor and settings are full screen, so the bottom bar steps aside.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = destination.id != R.id.navigation_note_editor &&
                destination.id != R.id.navigation_settings
            ViewCompat.requestApplyInsets(binding.root)
        }

        // Schedule hourly verse notifications if enabled
        maybeScheduleHourlyNotifications()
    }

    private fun maybeScheduleHourlyNotifications() {
        val prefs = ReaderPrefs(this)
        if (prefs.notificationsEnabled) {
            VerseBootReceiver().scheduleHourlyNotification(this)
        }
    }
}