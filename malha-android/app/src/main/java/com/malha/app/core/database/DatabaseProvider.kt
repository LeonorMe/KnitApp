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
                    MalhaDatabase.MIGRATION_7_8
                )
                // TODO  Remove before production (rite proper Migration)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                .also { database = it }

                /* TODO Long-Term: Disable in Production
                val builder = Room.databaseBuilder(context, AppDatabase::class.java, "malha.db")

                if (BuildConfig.DEBUG) {
                    builder.fallbackToDestructiveMigration(dropAllTables = true)
                } else {
                    builder.addMigrations(MIGRATION_1_2, MIGRATION_2_3 /* ... */)
                }

                val db = builder.build()
                */
        }
    }
}
