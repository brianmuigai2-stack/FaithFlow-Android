package com.example.faithflow_bible

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.data.ThemeMode
import com.example.faithflow_bible.databinding.ActivityMainBinding
import com.example.faithflow_bible.notification.scheduleHourlyNotifications

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = ReaderPrefs(this)
        AppCompatDelegate.setDefaultNightMode(
            when (prefs.themeMode) {
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = destination.id != R.id.navigation_note_editor &&
                destination.id != R.id.navigation_settings &&
                destination.id != R.id.navigation_info
        }

        maybeScheduleHourlyNotifications()
    }

    private fun maybeScheduleHourlyNotifications() {
        val prefs = ReaderPrefs(this)
        if (prefs.notificationsEnabled) {
            scheduleHourlyNotifications(this)
        }
    }
}
