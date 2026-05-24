package com.example.front

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.front.data.di.StorageEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import java.util.concurrent.Executors

@HiltAndroidApp
class FrontApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(applyRussianLocale(base))
    }

    override fun onCreate() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ru"))
        super.onCreate()
        Executors.newSingleThreadExecutor().execute {
            EntryPointAccessors.fromApplication(this, StorageEntryPoint::class.java)
                .tokenStorage()
                .warmUp()
        }
    }

    private fun applyRussianLocale(context: Context): Context {
        val locale = Locale.forLanguageTag("ru")
        val config = Configuration(context.resources.configuration).apply {
            setLocale(locale)
            setLocales(LocaleList(locale))
        }
        return context.createConfigurationContext(config)
    }
}
