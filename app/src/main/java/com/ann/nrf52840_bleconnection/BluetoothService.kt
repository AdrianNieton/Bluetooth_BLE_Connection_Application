package com.ann.nrf52840_bleconnection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class BluetoothService(private val context: Context) {

    private var mActivity: MainActivity? = context as? MainActivity
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetoothManager: BluetoothManager

    private lateinit var mPairedDevices: Set<BluetoothDevice>

    companion object {
        const val REQUEST_ENABLE_BLUETOOTH = 1
    }


    @SuppressLint("MissingPermission")
    fun setup(): Boolean {
        mBluetoothManager = context.getSystemService(BluetoothManager::class.java)
        mBluetoothAdapter = mBluetoothManager.adapter

        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return false
        }
        else {
            if (!mBluetoothAdapter.isEnabled) {
                mBluetoothAdapter.enable()
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                mActivity?.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
            }
            return true
        }
    }

    @SuppressLint("MissingPermission", "ResourceType")
    fun pairedDeviceList(): MutableList<BluetoothDevice> {
        mPairedDevices = mBluetoothAdapter.bondedDevices
        Log.i("DEBUGGG", "Paired list: $mPairedDevices")

        val deviceList: MutableList<BluetoothDevice> = mutableListOf()

        if (mPairedDevices.isNotEmpty()) {
            mPairedDevices.forEach { device ->
                deviceList.add(device)
                Log.i("device", ""+device)
            }
        }
        return deviceList
    }

    @SuppressLint("MissingPermission")
    fun discoverDevices() {
        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(BluetoothReceiver(),filter)
        mBluetoothAdapter.startDiscovery()
    }
}