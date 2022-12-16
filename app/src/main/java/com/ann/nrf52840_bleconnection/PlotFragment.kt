package com.ann.nrf52840_bleconnection

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.ann.nrf52840_bleconnection.databinding.FragmentPlotBinding
import com.google.common.math.Stats
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.*
import kotlin.math.round


class PlotFragment : Fragment() {

    private lateinit var mBinding: FragmentPlotBinding
    private var mActivity: MainActivity? = null
    private val db = Database()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentPlotBinding.inflate(layoutInflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.cloud_title)
        //plot = mBinding.plot
        setHasOptionsMenu(true)

        val result = db.getData()
        val temperatureList = arrayListOf<Float>()
        val humidityList = arrayListOf<Float>()

        // Print results from select statement
        while (result!!.next()) {
            temperatureList.add(result.getFloat(1))
            humidityList.add(result.getFloat(2))
        }

        var graph = mBinding.graph
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true

        val series: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()
        val series2: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()

        temperatureList.zip(humidityList).forEachIndexed { index, it ->
            run {
                Log.i("Index", index.toString())
                series.appendData(DataPoint(index.toDouble(), it.component1().toDouble()), true, temperatureList.size)
                series2.appendData(DataPoint(index.toDouble(), it.component2().toDouble()), true, humidityList.size)
            }
        }

        series.color = Color.rgb(0, 80, 100)
        series.title = "Temperature"
        series.isDrawDataPoints = true
        series.dataPointsRadius = 5f
        series.thickness = 1

        series2.color = Color.rgb(100, 80, 0)
        series2.title = "Humidity"
        series2.isDrawDataPoints = true
        series2.dataPointsRadius = 5f
        series2.thickness = 1

        graph.addSeries(series)
        graph.addSeries(series2)

        graph.title = "Samples"
        graph.titleTextSize = 50f
        graph.titleColor = Color.RED
        graph.legendRenderer.isVisible = true
        graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        val gridLabel = graph.gridLabelRenderer
        gridLabel.horizontalAxisTitle = "Days"
        gridLabel.verticalAxisTitle = "Mesure"
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

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        //mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        //mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(false)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}