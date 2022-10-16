package com.ann.nrf52840_bleconnection

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.ann.nrf52840_bleconnection.BluetoothService.Companion.REQUEST_ENABLE_BLUETOOTH

class BleDeviceScan(private val context: Context): Service() {

    private var mActivity: MainActivity? = context as? MainActivity
    private var isScanning = false
    private val handler = Handler()
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetoothManager: BluetoothManager
    private lateinit var mBluetoothLeScanner: BluetoothLeScanner
    var mBluetoothGatt: BluetoothGatt? = null
    private val binder = LocalBinder()

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000


    private val mBluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun setup(): Boolean {
        mBluetoothManager = context.getSystemService(BluetoothManager::class.java)
        mBluetoothAdapter = mBluetoothManager.adapter
        mBluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner

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

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            mActivity?.addDevice(result.device)
        }
    }

    @SuppressLint("MissingPermission")
     fun scanLeDevice() {
        if (!isScanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                isScanning = false
                mBluetoothLeScanner.stopScan(leScanCallback)
                mBluetoothLeScanner.startScan(leScanCallback)
            }, SCAN_PERIOD)
            isScanning = true
            mBluetoothLeScanner.startScan(leScanCallback)
        } else {
            isScanning = false
            mBluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        mBluetoothAdapter.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // connect to the GATT server on the device
                mBluetoothGatt = device.connectGatt(this, false, mBluetoothGattCallback)
                return true
            } catch (exception: IllegalArgumentException) {
                Log.w("BLE", "Device not found with provided address.  Unable to connect.")
                return false
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService() : BleDeviceScan {
            return this@BleDeviceScan
        }
    }
}