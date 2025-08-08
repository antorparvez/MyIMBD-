package com.myimbd.app

import android.app.Application
import com.myimbd.app.util.ThemeManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyIMBDApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply saved theme at app start; default is light (false)
        ThemeManager.applySavedTheme(this)
    }
}
