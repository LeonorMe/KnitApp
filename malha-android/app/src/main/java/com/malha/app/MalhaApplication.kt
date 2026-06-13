package com.malha.app

import android.app.Application
import com.malha.app.core.app.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MalhaApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContainer = AppContainer(this)
        applicationScope.launch {
            appContainer.databaseSeeder.seed()
        }
    }

    companion object {
        lateinit var instance: MalhaApplication
            private set
    }
}

