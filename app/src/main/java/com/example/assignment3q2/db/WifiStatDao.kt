package com.example.assignment3q2.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WifiStatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wifiStat: WifiStat)

    @Query("SELECT * FROM wifi_stats WHERE location = :location")
    suspend fun getStatsForLocation(location: String): List<WifiStat>
}
