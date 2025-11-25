package com.example.kkobakkobak.ui.mood

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.model.MoodLog
import com.example.kkobakkobak.databinding.FragmentMoodBinding
import com.example.kkobakkobak.ui.history.LogHistoryActivity
import com.example.kkobakkobak.ui.log.LogActivity
import com.example.kkobakkobak.ui.record.RecordActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    private val moodViewModel: MoodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupSwipeToRefresh()
        observeViewModel()
        setupChart()

        fetchMoodData()
    }

    private fun setupClickListeners() {
        binding.btnAddLogMoodTab.setOnClickListener {
            startActivity(Intent(requireContext(), LogActivity::class.java))
        }

        binding.btnViewHistoryMoodTab.setOnClickListener {
            startActivity(Intent(requireContext(), LogHistoryActivity::class.java))
        }

        binding.btnRecordMood.setOnClickListener {
            startActivity(Intent(requireContext(), RecordActivity::class.java))
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchMoodData()
        }
    }

    private fun observeViewModel() {
        moodViewModel.moodLogs.observe(viewLifecycleOwner) { moodLogs ->
            if (moodLogs.isNullOrEmpty()) {
                // Show empty state with Lottie animation and message
                binding.moodLineChart.visibility = View.GONE
                binding.emptyStateLayout.visibility = View.VISIBLE // Assuming you have a layout with this ID
                binding.emptyStateLottie.playAnimation() // Assuming you have a Lottie view with this ID
            } else {
                binding.moodLineChart.visibility = View.VISIBLE
                binding.emptyStateLayout.visibility = View.GONE
                updateChart(moodLogs)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun fetchMoodData() {
        moodViewModel.fetchMoodLogs()
    }

    private fun setupChart() {
        binding.moodLineChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setDrawGridBackground(false)
            isHighlightPerDragEnabled = true
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = getThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant)
                granularity = 1f
            }

            axisLeft.apply {
                textColor = getThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant)
                setDrawGridLines(true)
                gridColor = getThemeColor(com.google.android.material.R.attr.colorOutline)
            }
            axisRight.isEnabled = false
        }
    }

    private fun updateChart(moodLogs: List<MoodLog>) {
        val entries = moodLogs.mapIndexed { index, log ->
            Entry(index.toFloat(), log.mood.toFloat())
        }

        val dataSet = LineDataSet(entries, "Mood").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            setDrawValues(false)
            setDrawCircles(true)
            
            val primaryColor = getThemeColor(androidx.appcompat.R.attr.colorPrimary)
            color = primaryColor
            setCircleColor(primaryColor)
            circleRadius = 4f
            circleHoleRadius = 2f

            val gradient = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient)
            fillDrawable = gradient
        }

        val lineData = LineData(dataSet)
        binding.moodLineChart.data = lineData

        val dates = moodLogs.map { log ->
            SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(log.date))
        }
        binding.moodLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        binding.moodLineChart.xAxis.labelCount = if (dates.size > 7) 7 else dates.size
        binding.moodLineChart.invalidate()
    }

    @ColorInt
    private fun getThemeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
