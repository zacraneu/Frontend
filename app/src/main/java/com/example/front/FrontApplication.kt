package com.example.front

import android.app.Application
import com.example.front.data.di.StorageEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executors

@HiltAndroidApp
class FrontApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // EncryptedSharedPreferences can initialize slowly on cold start.
        Executors.newSingleThreadExecutor().execute {
            EntryPointAccessors.fromApplication(this, StorageEntryPoint::class.java)
                .tokenStorage()
                .warmUp()
        }
    }
}
