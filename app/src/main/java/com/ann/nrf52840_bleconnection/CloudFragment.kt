package com.ann.nrf52840_bleconnection

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ann.nrf52840_bleconnection.databinding.FragmentCloudBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CloudFragment : Fragment() {

    private lateinit var mBinding: FragmentCloudBinding
    private var mActivity: MainActivity? = null
    val db = Database()

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

        val minT = db.getMinTemperatureValue()
        val minH = db.getMinHumidityValue()
        val maxT = db.getMaxTemperatureValue()
        val maxH = db.getMaxHumidityValue()
        val meanT = db.getMeanTemperatureValue()
        val meanH = db.getMeanHumidityValue()
        val varianceT = db.getVarianceTemperatureValue()
        val varianceH = db.getVarianceHumidityValue()

        showMinValues(minT, minH)
        showMaxValues(maxT, maxH)
        showMeanValues(meanT, meanH)
        showVarianceValues(varianceT,varianceH)
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
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}