package com.ekh.githubrepo

import android.app.Application
import com.ekh.githubrepo.util.Flipper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Flipper.init(this)
    }
}