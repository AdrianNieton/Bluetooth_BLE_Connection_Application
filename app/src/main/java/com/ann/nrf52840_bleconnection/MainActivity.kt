package com.ann.nrf52840_bleconnection

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ann.nrf52840_bleconnection.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener, OnReceiveListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: RecyclerViewAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    private val mBLEScanner = BleDeviceScan(this)
    private val mBTService = BluetoothService(this)

    private val REQUEST_ACCESS_FINE_LOCATION = 102

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpBluetooth()
        setupRecyclerView()

        mBinding.refreshButton.setOnClickListener {
            getDevices()
            mBTService.discoverDevices()
            mBLEScanner.scanLeDevice()
        }

    }

    private fun setupRecyclerView() {
        mAdapter = RecyclerViewAdapter(mutableListOf(), this)
        mLayoutManager = LinearLayoutManager(this)
        getDevices()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mAdapter
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpBluetooth() {
        checkPermissions()
        val isBleOk = mBLEScanner.setup()
        val isOk = mBTService.setup()

        if (!isOk || !isBleOk) {
           // Device doesn't support Bluetooth
            Snackbar.make(mBinding.root, getString(R.string.bluetooth_not_supported), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun getDevices() {
        val pairedDevices = mBTService.pairedDeviceList()
        if (pairedDevices.isEmpty()) {
            Snackbar.make(mBinding.root, getString(R.string.bt_device_not_founded), Snackbar.LENGTH_SHORT).show()
        }

        val devicesList: MutableList<BluetoothDevice> = mutableListOf()
        pairedDevices.forEach { devicesList.add(it) }
        mAdapter.setDevices(devicesList)
    }

    private fun checkPermissions() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            when(ContextCompat.checkSelfPermission(
                baseContext,Manifest.permission.ACCESS_FINE_LOCATION
            )){
                PackageManager.PERMISSION_DENIED -> androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Runtime Permission")
                    .setMessage("Give Permission")
                    .setNeutralButton("Okay", DialogInterface.OnClickListener{ _, _ ->
                        if(ContextCompat.checkSelfPermission(baseContext,Manifest.permission.ACCESS_FINE_LOCATION)!=
                            PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_ACCESS_FINE_LOCATION)
                        }
                    })
                    .show()

                PackageManager.PERMISSION_GRANTED ->{
                    Log.d("discoverDevices","Permission Granted")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onClick(device: BluetoothDevice) {
        device.createBond()
        mBLEScanner.connect(device.address)
    }

    override fun onLongClick(device: BluetoothDevice) {

    }

    override fun addDevice(device: BluetoothDevice) {
        mAdapter.add(device)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(BluetoothReceiver())
    }
}