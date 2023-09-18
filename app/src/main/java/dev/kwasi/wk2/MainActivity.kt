package dev.kwasi.wk2

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var wifiManager: WifiManager
    private lateinit var recyclerView: RecyclerView
    private val requestCode = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION),requestCode);
        }
        else {
            scanForNetworks();
        }
    }

    private fun scanForNetworks(){
        val wifiNetworks = getWifiNetworks()
        val adapter = WifiListAdapter(wifiNetworks)
        recyclerView.adapter = adapter

    }

    @SuppressLint("MissingPermission")
    private fun getWifiNetworks(): List<WifiNetwork> {
        val wifiScanList = wifiManager.scanResults
        val wifiNetworks = mutableListOf<WifiNetwork>()

        for (scanResult in wifiScanList) {
            val wifiNetwork = WifiNetwork(scanResult.SSID, scanResult.level)
            wifiNetworks.add(wifiNetwork)
        }

        return wifiNetworks
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            this.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    scanForNetworks()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}