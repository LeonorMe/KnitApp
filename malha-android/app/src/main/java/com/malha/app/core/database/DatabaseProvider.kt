package com.malha.app.core.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var database: MalhaDatabase? = null

    fun getDatabase(context: Context): MalhaDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                MalhaDatabase::class.java,
                "malha.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { database = it }
        }
    }
}
