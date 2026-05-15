package com.malha.app

import android.app.Application
import com.malha.app.core.app.AppContainer

class MalhaApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = AppContainer(this)
    }

    companion object {
        lateinit var instance: MalhaApplication
            private set
    }
}

