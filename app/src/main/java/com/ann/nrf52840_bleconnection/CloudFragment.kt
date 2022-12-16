package com.ann.nrf52840_bleconnection

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope

import com.ann.nrf52840_bleconnection.databinding.FragmentCloudBinding
import com.google.common.math.Stats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextFloat

class CloudFragment : Fragment(), MainAux {

    private lateinit var mBinding: FragmentCloudBinding
    private var mActivity: MainActivity? = null
    private val db = Database()

    private val newTemperatures = mutableListOf<Float>()
    private val newHumidity = mutableListOf<Float>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentCloudBinding.inflate(layoutInflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.cloud_title)
        setHasOptionsMenu(true)


        mBinding.fab2.setOnClickListener { launchPlotFragment() }

        val result = db.getData()
        val temperatureList = arrayListOf<Float>()
        val humidityList = arrayListOf<Float>()

        // Print results from select statement
        while (result!!.next()) {
            temperatureList.add(result.getFloat(1))
            humidityList.add(result.getFloat(2))
        }

        val minT = Collections.min(temperatureList).toString()
        val minH =  Collections.min(humidityList).toString()
        val maxT = Collections.max(temperatureList).toString()
        val maxH = Collections.max(humidityList).toString()
        val meanT = round(Stats.meanOf(temperatureList)).toString()
        val meanH = round(Stats.meanOf(humidityList)).toString()
        val varianceT = round(calculateStandardDeviation(temperatureList)).toString()
        val varianceH = round(calculateStandardDeviation(humidityList)).toString()

        showMinValues(minT, minH)
        showMaxValues(maxT, maxH)
        showMeanValues(meanT, meanH)
        showVarianceValues(varianceT,varianceH)

    }

    private fun insertNewValues() {
        lifecycle.coroutineScope.launch {
            withContext(Dispatchers.IO) {
                var randomTemp: Float
                var randomHum: Float
                while (newTemperatures.size < 5 || newHumidity.size < 5) {
                    randomTemp = nextFloat()
                    randomHum = nextFloat()

                    if (randomTemp <= 0.5 && newTemperatures.size < 5) {
                        randomTemp = (randomTemp*100)
                        newTemperatures.add(randomTemp)
                    }

                    if (randomHum <= 0.5 && newHumidity.size < 5) {
                        randomHum = (randomHum*100)
                        newHumidity.add(randomHum)
                    }
                }

                db.insertData(newTemperatures, newHumidity)
            }
        }
    }

    private fun showVarianceValues(varianceT: String, varianceH: String) {
        mBinding.tvStdTResult.text = varianceT
        mBinding.tvStdHResult.text = varianceH
    }

    private fun showMeanValues(meanT: String, meanH: String) {
        mBinding.tvResultTemperature.text = meanT
        mBinding.tvResultHumidity.text = meanH
    }

    private fun showMaxValues(maxT: String, maxH: String) {
        mBinding.tvMaxTResult.text = maxT
        mBinding.tvMaxHResult.text = maxH
    }

    private fun showMinValues(minT: String, minH: String) {
        mBinding.tvMinTResult.text = minT
        mBinding.tvMinHResult.text = minH
    }

    fun calculateStandardDeviation(array: ArrayList<Float>): Double {

        // get the sum of array
        var sum = 0.0
        for (i in array) {
            sum += i
        }

        // get the mean of array
        val length = array.size
        val mean = sum / length

        // calculate the standard deviation
        var standardDeviation = 0.0
        for (num in array) {
            standardDeviation += (num - mean).pow(2.0)
        }
        return sqrt(standardDeviation / length)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                mActivity?.onBackPressed()
                true
            }
            else -> {
                return false
            }
        }
    }

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun launchPlotFragment() {
        val fragment = PlotFragment()

        val fragmentManager = mActivity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()

        fragmentTransaction?.add(R.id.clRoot, fragment)
        fragmentTransaction?.addToBackStack(null) //Destruir fragment al dar hacia atras
        fragmentTransaction?.commit()

        //mBinding.fab.hide()
        hideFab()
    }

    override fun hideFab(isVisible: Boolean) {
        if (!isVisible) {
            mBinding.fab2.show()
        } else {
            mBinding.fab2.hide()
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        insertNewValues()
    }
}