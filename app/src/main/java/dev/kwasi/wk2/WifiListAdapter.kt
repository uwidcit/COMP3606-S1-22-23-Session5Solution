package dev.kwasi.wk2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log

class WifiListAdapter(private val wifiNetworks: List<WifiNetwork>) :
    RecyclerView.Adapter<WifiListAdapter.ViewHolder>(), View.OnClickListener {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ssidTextView: TextView = itemView.findViewById(R.id.ssidTextView)
        val strengthTextView: TextView = itemView.findViewById(R.id.strengthTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wifi, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wifiNetwork = wifiNetworks[position]

        holder.ssidTextView.text = wifiNetwork.ssid
        holder.strengthTextView.text = "Signal Strength: ${wifiNetwork.signalStrength} dBm"
    }

    override fun getItemCount(): Int {
        return wifiNetworks.size
    }

    override fun onClick(v: View) {
        val ssidTextView = v.findViewById<TextView>(R.id.ssidTextView)
        Log.e("CLICK", "I clicked the view: SSID = " + ssidTextView.text);
    }
}
