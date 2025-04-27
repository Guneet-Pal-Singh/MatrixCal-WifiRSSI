package com.example.assignment3q2.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wifi_stats")
data class WifiStat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val location: String,
    val ssid: String,
    val avgRssi: Double,
    val minRssi: Int,
    val maxRssi: Int
)
