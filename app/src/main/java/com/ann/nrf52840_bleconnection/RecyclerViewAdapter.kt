package com.ann.nrf52840_bleconnection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ann.nrf52840_bleconnection.databinding.ItemDeviceBinding

class RecyclerViewAdapter(private var devices: MutableList<BluetoothDevice>, private var listener: OnClickListener):
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    companion object {
        private const val BOND_NONE = 10
        private const val BOND_BONDING = 11
        private const val BOND_BONDED = 12
    }
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_device, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]

        with(holder) {
            setListener(device)

            binding.tvDeviceName.text = if (device.name == null) "null" else device.name
            binding.tvDeviceMac.text = device.address
            when(device.bondState) {
                BOND_NONE -> binding.tvBondState.text = "UNPAIRED"
                BOND_BONDING -> binding.tvBondState.text = "PAIRING"
                BOND_BONDED -> binding.tvBondState.text = "PAIRED"
            }
        }
    }

    override fun getItemCount(): Int = devices.size

    fun setDevices(devices: MutableList<BluetoothDevice>) {
        this.devices = devices
        notifyDataSetChanged()
    }

    fun add(device: BluetoothDevice) {
        if (!devices.contains(device)) {
            devices.add(device)
            notifyItemInserted(devices.size - 1)
        }
    }


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemDeviceBinding.bind(view)

        fun setListener(device: BluetoothDevice) {
            with(binding.root) {

                setOnClickListener {
                    listener.onClick(device)
                    notifyDataSetChanged()
                }

                setOnLongClickListener {
                    listener.onLongClick(device)
                    true
                }
            }
        }
    }
}