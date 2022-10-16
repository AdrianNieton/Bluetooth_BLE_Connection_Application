package com.ann.nrf52840_bleconnection

import android.bluetooth.BluetoothDevice

interface OnClickListener {
    fun onClick(device: BluetoothDevice)
    fun onLongClick(device: BluetoothDevice)
}