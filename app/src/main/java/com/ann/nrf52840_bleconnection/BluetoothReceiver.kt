package com.ann.nrf52840_bleconnection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothReceiver: BroadcastReceiver() {

    private var mActivity: MainActivity? = null

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = ""
        if(intent!=null){
            action = intent.action.toString()
        }

        when (action) {
            BluetoothDevice.ACTION_FOUND -> {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                val device: BluetoothDevice? =
                    intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                Log.i("ENTRA", device.toString())
                mActivity = context as? MainActivity
                device?.let { mActivity?.addDevice(it) }
            }
            BluetoothAdapter.ACTION_STATE_CHANGED ->{
                Log.d("discoverDevices1","STATE CHANGED")
            }
            BluetoothAdapter.ACTION_DISCOVERY_STARTED ->{
                Log.d("discoverDevices2","Discovery Started")
            }
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                Log.d("discoverDevices3","Disvcoery Fininshed")
            }
        }
    }
}