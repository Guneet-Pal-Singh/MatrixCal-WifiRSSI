package com.example.assignment3q2

import android.app.Application
import android.content.*
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import androidx.compose.runtime.*
import com.example.assignment3q2.db.WifiStat
import com.example.assignment3q2.db.WifiStatDao

data class WifiInfo(val ssid: String, val rssi: Int)

class WifiViewModel(
    application: Application,
    private val wifiStatDao: WifiStatDao
) : AndroidViewModel(application) {
    private val wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager

    var currentLocation by mutableStateOf("Location 1")
        private set

    var rssiMatrix = mutableStateMapOf(
        "Location 1" to mutableStateListOf<List<WifiInfo>>(),
        "Location 2" to mutableStateListOf<List<WifiInfo>>(),
        "Location 3" to mutableStateListOf<List<WifiInfo>>()
    )
        private set

    var isScanning by mutableStateOf(false)
        private set

    var scanCount by mutableStateOf(0)
        private set

    val readingsPerLocation = 100

    private var scanReceiver: BroadcastReceiver? = null
    private var scanJob: Job? = null
    private var lastScanResults: List<WifiInfo> = emptyList()

    fun selectLocation(location: String) {
        currentLocation = location
    }

    fun startScan(context: Context) {
        if (isScanning) return
        isScanning = true
        scanCount = 0
        rssiMatrix[currentLocation]?.clear()
        registerReceiver(context)
        startScanLoop(context)
    }

    fun stopScan(context: Context) {
        isScanning = false
        scanJob?.cancel()
        unregisterReceiverSafely(context)
        saveAveragesToDb()
    }

    private fun startScanLoop(context: Context) {
        scanJob = CoroutineScope(Dispatchers.Default).launch {
            while (isScanning && scanCount < readingsPerLocation) {
                withContext(Dispatchers.Main) {
                    val hasPermission = ActivityCompat.checkSelfPermission(
                        context, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        wifiManager.startScan()
                    } else {
                        addScanResult(useLast = true, context)
                    }
                }

                delay(500)

                withContext(Dispatchers.Main) {
                    if (scanCount < readingsPerLocation) {
                        addScanResult(useLast = true, context)
                    }
                }

                delay(500)
            }
            withContext(Dispatchers.Main) {
                stopScan(context)
            }
        }
    }

    private fun addScanResult(useLast: Boolean, context: Context?) {
        if (scanCount >= readingsPerLocation) {
            if (isScanning) stopScan(context ?: getApplication())
            return
        }

        val hasPermission = context?.let {
            ActivityCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION)
        } == PackageManager.PERMISSION_GRANTED

        val scanResults = if (useLast || !hasPermission) {
            lastScanResults
        } else {
            wifiManager.scanResults
                .filter { it.SSID.isNotBlank() }
                .map { WifiInfo(it.SSID, it.level) }
                .also { results -> lastScanResults = results }
        }

        if (scanResults.isNotEmpty()) {
            rssiMatrix[currentLocation]?.add(scanResults)
            scanCount++
        }

        if (scanCount >= readingsPerLocation) {
            if (isScanning) stopScan(context ?: getApplication())
        }
    }

    private fun registerReceiver(context: Context) {
        if (scanReceiver != null) return

        scanReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                CoroutineScope(Dispatchers.Default).launch {
                    ctx ?: return@launch

                    val hasPermission = ActivityCompat.checkSelfPermission(
                        ctx, android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasPermission) {
                        isScanning = false
                        unregisterReceiverSafely(ctx)
                        return@launch
                    }

                    val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
                    if (success) {
                        val results = wifiManager.scanResults
                            .filter { it.SSID.isNotBlank() }
                            .map { WifiInfo(it.SSID, it.level) }
                        lastScanResults = results
                        addScanResult(useLast = false, ctx)
                    } else {
                        addScanResult(useLast = true, ctx)
                    }
                }
            }
        }

        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(scanReceiver, intentFilter)
    }

    private fun unregisterReceiverSafely(context: Context) {
        try {
            scanReceiver?.let {
                context.unregisterReceiver(it)
                scanReceiver = null
            }
        } catch (_: Exception) { }
    }

    override fun onCleared() {
        super.onCleared()
        unregisterReceiverSafely(getApplication())
        scanJob?.cancel()
    }

    // Returns a map of SSID to Triple(average, min, max)
    fun getWifiStatisticsWithRange(): Map<String, Triple<Double, Int, Int>> {
        val allScans = rssiMatrix[currentLocation] ?: return emptyMap()
        val rssiValuesPerSSID = mutableMapOf<String, MutableList<Int>>()
        for (scan in allScans) {
            for (wifiInfo in scan) {
                rssiValuesPerSSID.getOrPut(wifiInfo.ssid) { mutableListOf() }
                    .add(wifiInfo.rssi)
            }
        }
        return rssiValuesPerSSID.mapValues { (_, rssiList) ->
            val avg = rssiList.average()
            val min = rssiList.minOrNull() ?: 0
            val max = rssiList.maxOrNull() ?: 0
            Triple(avg, min, max)
        }
    }

    private fun saveAveragesToDb() {
        val stats = getWifiStatisticsWithRange()
        viewModelScope.launch(Dispatchers.IO) {
            stats.forEach { (ssid, statTriple) ->
                val avg = statTriple.first
                val min = statTriple.second
                val max = statTriple.third
                val wifiStat = WifiStat(
                    location = currentLocation,
                    ssid = ssid,
                    avgRssi = avg,
                    minRssi = min,
                    maxRssi = max
                )
                wifiStatDao.insert(wifiStat)
            }
        }
    }

    suspend fun getStatsForLocation(location: String): List<WifiStat> {
        return wifiStatDao.getStatsForLocation(location)
    }
}

class WifiViewModelFactory(
    private val app: Application,
    private val wifiStatDao: WifiStatDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiViewModel(app, wifiStatDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
