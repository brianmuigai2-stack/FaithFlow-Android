package com.example.faithflow_bible

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.faithflow_bible.databinding.ActivityMainBinding
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.notification.scheduleHourlyNotifications

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)

        // The note editor and settings are full screen, so the bottom bar steps aside.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.isVisible = destination.id != R.id.navigation_note_editor &&
                destination.id != R.id.navigation_settings
        }

        // Schedule hourly verse notifications if enabled
        maybeScheduleHourlyNotifications()
    }

    private fun maybeScheduleHourlyNotifications() {
        val prefs = ReaderPrefs(this)
        if (prefs.notificationsEnabled) {
            scheduleHourlyNotifications(this)
        }
    }
}