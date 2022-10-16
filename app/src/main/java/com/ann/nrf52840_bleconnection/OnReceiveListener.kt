package com.ann.nrf52840_bleconnection

import android.bluetooth.BluetoothDevice

interface OnReceiveListener {
    fun addDevice(device: BluetoothDevice)
}