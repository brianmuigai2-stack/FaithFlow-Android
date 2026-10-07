package com.example.faithflow_bible.ui.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.faithflow_bible.data.ReaderPrefs
import com.example.faithflow_bible.data.ThemeMode
import com.example.faithflow_bible.notification.VerseBootReceiver

data class SettingsUi(
    val theme: ThemeMode,
    val textSizeIndex: Int,
    val notificationsEnabled: Boolean,
    val versionName: String
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = ReaderPrefs(app)
    private val appLabel = versionLabel(app)

    private val _ui = MutableLiveData(
        SettingsUi(prefs.themeMode, prefs.textSizeIndex, prefs.notificationsEnabled, appLabel)
    )
    val ui: LiveData<SettingsUi> = _ui

    fun setTheme(mode: ThemeMode) {
        if (prefs.themeMode == mode) return
        prefs.themeMode = mode
        // setDefaultNightMode recreates the visible activities, so the change shows immediately.
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun setTextSize(index: Int) {
        if (prefs.textSizeIndex == index) return
        prefs.textSizeIndex = index
        _ui.value = SettingsUi(prefs.themeMode, prefs.textSizeIndex, prefs.notificationsEnabled, appLabel)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        if (prefs.notificationsEnabled == enabled) return
        prefs.notificationsEnabled = enabled
        if (enabled) {
            VerseBootReceiver().scheduleHourlyNotification(getApplication())
        } else {
            VerseBootReceiver().cancelSchedule(getApplication())
        }
        _ui.value = SettingsUi(prefs.themeMode, prefs.textSizeIndex, prefs.notificationsEnabled, appLabel)
    }

    fun clearMarks() = prefs.clearMarks()

    private companion object {
        fun versionLabel(app: Application): String =
            "FaithFlow " + app.packageManager.getPackageInfo(app.packageName, 0).versionName
    }
}