package com.example.kkobakkobak.ui.mood

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kkobakkobak.data.model.MoodLog
import com.example.kkobakkobak.databinding.FragmentMoodBinding
import com.example.kkobakkobak.ui.history.LogHistoryActivity
import com.example.kkobakkobak.ui.log.LogActivity
import com.example.kkobakkobak.ui.record.RecordActivity
import com.github.mikephil.charting.animation.Easing
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

        // [효과 1] 새로고침 로딩바 색상을 앱 테마색으로 설정
        binding.swipeRefreshLayout.setColorSchemeColors(
            getThemeColor(androidx.appcompat.R.attr.colorPrimary),
            getThemeColor(androidx.appcompat.R.attr.colorAccent)
        )

        setupClickListeners()
        setupSwipeToRefresh()
        observeViewModel()
        setupChart()

        fetchMoodData()
    }

    private fun setupClickListeners() {
        // [효과 2] 버튼 클릭 시 쫀득한 애니메이션 적용 (확장 함수 사용)
        binding.btnAddLogMoodTab.setOnClickWithAnimation {
            startActivityWithAnimation(Intent(requireContext(), LogActivity::class.java))
        }

        binding.btnViewHistoryMoodTab.setOnClickWithAnimation {
            startActivityWithAnimation(Intent(requireContext(), LogHistoryActivity::class.java))
        }

        binding.btnRecordMood.setOnClickWithAnimation {
            startActivityWithAnimation(Intent(requireContext(), RecordActivity::class.java))
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
            // 색상 참조 수정 (com.google -> androidx.appcompat 또는 android)
            xAxis.textColor = getThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant)

            axisLeft.textColor = getThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant)
            axisLeft.setDrawGridLines(true)
            axisRight.isEnabled = false

            // [효과 3] 차트 초기 실행 시 부드럽게 올라오는 애니메이션
            animateY(1500, Easing.EaseInOutCubic)
        }
    }

    private fun updateChart(moodLogs: List<MoodLog>) {
        // 데이터가 비어있으면 차트를 숨기고 빈 화면(Lottie)을 보여줌
        if (moodLogs.isEmpty()) {
            binding.chartLayout.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
            return
        } else {
            binding.chartLayout.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }

        val entries = moodLogs.mapIndexed { index, log ->
            Entry(index.toFloat(), log.mood.toFloat())
        }

        val dataSet = LineDataSet(entries, "Mood").apply {
            // [수정됨] colorPrimary 참조 에러 해결
            color = getThemeColor(androidx.appcompat.R.attr.colorPrimary)
            valueTextColor = getThemeColor(com.google.android.material.R.attr.colorOnSurface)
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

        // [효과 4] 데이터 갱신 시마다 애니메이션 실행
        binding.moodLineChart.animateY(1000, Easing.EaseInOutQuad)
        binding.moodLineChart.invalidate()
    }

    // [유틸리티] 화면 전환 시 부드러운 슬라이드 효과
    private fun startActivityWithAnimation(intent: Intent) {
        startActivity(intent)
        requireActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    @ColorInt
    private fun getThemeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        // context가 null일 경우를 대비해 안전하게 호출
        context?.theme?.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// [확장 함수] 버튼 클릭 애니메이션 (파일 최하단에 추가)
fun View.setOnClickWithAnimation(action: () -> Unit) {
    this.setOnClickListener {
        it.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
            it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            action()
        }.start()
    }
}