package dev.kwasi.wk2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WifiListAdapter(private val wifiNetworks: List<WifiNetwork>, private val wifiNetworkClick: WifiNetworkClick) :
    RecyclerView.Adapter<WifiListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ssidTextView: TextView = itemView.findViewById(R.id.ssidTextView)
        val strengthTextView: TextView = itemView.findViewById(R.id.strengthTextView)
        val encryptionStateTextView: TextView = itemView.findViewById(R.id.encryptionState)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wifi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wifiNetwork = wifiNetworks[position]

        holder.ssidTextView.text = wifiNetwork.ssid
        holder.strengthTextView.text = "Signal Strength: ${wifiNetwork.signalStrength} dBm"

        if (wifiNetwork.hasPassword)
            holder.encryptionStateTextView.text = "Passworded WiFi"
        else
            holder.encryptionStateTextView.text = "Open WiFi"

        holder.itemView.setOnClickListener {
            wifiNetworkClick.onWifiNetworkClicked(wifiNetwork)
        }
    }

    override fun getItemCount(): Int {
        return wifiNetworks.size
    }
}
