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
                .addMigrations(
                    MalhaDatabase.MIGRATION_6_7,
                    MalhaDatabase.MIGRATION_7_8,
                    MalhaDatabase.MIGRATION_10_11,
                    MalhaDatabase.MIGRATION_11_12,
                    MalhaDatabase.MIGRATION_12_13
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { database = it }
        }
    }
}
