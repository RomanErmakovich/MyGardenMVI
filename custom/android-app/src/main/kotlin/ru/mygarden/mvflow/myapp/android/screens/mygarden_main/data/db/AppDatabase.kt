package ru.mygarden.mvflow.myapp.android.screens.mygarden_main.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = arrayOf(ParamBean::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paramDao(): ParamDao
}
/*
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<AppDatabase>(
                    context,
                    AppDatabase::class.java,
                    "readout_db"
                )
                    //.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
            }
            return INSTANCE
    }

        fun destroyInstance() {
            INSTANCE = null
        }
*/


