package dev.kwasi.wk2

import PasswordDialogFragment
import PasswordDialogListener
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var wifiManager: WifiManager
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var recyclerView: RecyclerView
    private val requestCode = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        val adapter = WifiListAdapter(wifiNetworks, object : WifiNetworkClick{
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onWifiNetworkClicked(wifiNetwork: WifiNetwork) {
                if (wifiNetwork.hasPassword){
                    showPasswordDialog(wifiNetwork)
                } else {
                    connectToOpenWifi(wifiNetwork)
                }
                Log.e("CLICK","I clicked the Wifi Network: ${wifiNetwork.ssid}")
            }
        })
        recyclerView.adapter = adapter
    }

    private fun showPasswordDialog(wifiNetwork: WifiNetwork) {
        val passwordDialog = PasswordDialogFragment()

        passwordDialog.setListener(object : PasswordDialogListener {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onPasswordEntered(password: String) {
                Log.e("PASSWORD", "I entered a password as $password. I should connect to ${wifiNetwork.ssid} with it")
                connectToPasswordWifi(wifiNetwork, password);
            }

            override fun onCancel() {
                Log.e("PASSWORD", "ICancelled the dialog. I dont want to  connect to the networkanymore")
            }
        })

        passwordDialog.show(supportFragmentManager, "password_dialog")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToPasswordWifi(wifiNetwork: WifiNetwork, password: String) {
        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setWpa2Passphrase(password)
            .setSsid(wifiNetwork.ssid)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Binds the application to the connected network (stops android from disconnecting from a no-internet network)
                connectivityManager.bindProcessToNetwork(network)
                Log.e("CONNECTED", "SUCCESSFULLY CONNECTED TO WIFI")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // This is to stop the looping request for OnePlus & Xiaomi models
                connectivityManager.bindProcessToNetwork(null)
                Log.e("DISCONNECTED", "DISCONNECTED FROM WIFI")
            }
        }

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToOpenWifi(wifiNetwork: WifiNetwork){
        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(wifiNetwork.ssid)
            .build()

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Binds the application to the connected network (stops android from disconnecting from a no-internet network)
                connectivityManager.bindProcessToNetwork(network)
                Log.e("CONNECTED", "SUCCESSFULLY CONNECTED TO WIFI")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // This is to stop the looping request for OnePlus & Xiaomi models
                connectivityManager.bindProcessToNetwork(null)
                Log.e("DISCONNECTED", "DISCONNECTED FROM WIFI")
            }
        }

        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }



    @SuppressLint("MissingPermission")
    private fun getWifiNetworks(): List<WifiNetwork> {
        val wifiScanList = wifiManager.scanResults
        val wifiNetworks = mutableListOf<WifiNetwork>()

        for (scanResult in wifiScanList) {

            // A Wifi Network has a password if it is [WEP, WPA or WPA2]
            val networkCapabilities = scanResult.capabilities
            val hasPassword = networkCapabilities.contains("WEP") || networkCapabilities.contains("WPA") || networkCapabilities.contains("WPA2")

            val wifiNetwork = WifiNetwork(scanResult.SSID, scanResult.level, hasPassword)
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