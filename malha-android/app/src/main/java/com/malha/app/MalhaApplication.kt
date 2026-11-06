package com.malha.app

import android.app.Application
import android.util.Log
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
            runCatching {
                appContainer.databaseSeeder.seed()
            }.onFailure { error ->
                Log.e("MalhaApplication", "Database seeding error", error)
            }
        }
    }

    companion object {
        lateinit var instance: MalhaApplication
            private set
    }
}
