package com.myimbd.app.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFERENCES_NAME = "app_settings"
    private const val KEY_DARK_MODE = "dark_mode"

    fun isDarkModeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, enableDarkMode: Boolean) {
        val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, enableDarkMode).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (enableDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun toggleTheme(context: Context) {
        setDarkMode(context, !isDarkModeEnabled(context))
    }

    fun applySavedTheme(context: Context) {
        val isDark = isDarkModeEnabled(context)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}


