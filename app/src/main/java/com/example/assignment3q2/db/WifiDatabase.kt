package com.example.assignment3q2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WifiStat::class], version = 1)
abstract class WifiDatabase : RoomDatabase() {
    abstract fun wifiStatDao(): WifiStatDao

    companion object {
        @Volatile
        private var INSTANCE: WifiDatabase? = null

        fun getInstance(context: Context): WifiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WifiDatabase::class.java,
                    "wifi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
