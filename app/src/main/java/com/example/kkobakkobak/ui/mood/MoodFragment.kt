package com.example.kkobakkobak.ui.mood

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            updateChart(moodLogs)
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

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)

            axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false
        }
    }

    private fun updateChart(moodLogs: List<MoodLog>) {
        if (moodLogs.isEmpty()) {
            binding.moodLineChart.clear()
            return
        }

        val entries = moodLogs.mapIndexed { index, log ->
            Entry(index.toFloat(), log.mood.toFloat())
        }

        val dataSet = LineDataSet(entries, "Mood").apply {
            color = ContextCompat.getColor(requireContext(), R.color.accent_blue)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
            setCircleColor(color)
            circleRadius = 4f
            lineWidth = 2f
            setDrawValues(false)
        }

        val lineData = LineData(dataSet)
        binding.moodLineChart.data = lineData

        val dates = moodLogs.map { log ->
            SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(log.date))
        }
        binding.moodLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        binding.moodLineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
