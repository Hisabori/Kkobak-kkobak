package com.example.kkobakkobak.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.example.kkobakkobak.databinding.FragmentHomeBinding
import com.example.kkobakkobak.ui.log.LogActivity
import com.example.kkobakkobak.ui.history.LogHistoryActivity
import com.example.kkobakkobak.ui.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ 오늘 복용 현황 텍스트 업데이트
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            val todayIntakes = db.medicationIntakeDao().getTodayIntakeList()
                val taken = todayIntakes.map { it.medicationName }

            val times = listOf("아침", "점심", "저녁", "취침전")
            val result = times.joinToString("\n") {
                "$it: ${if (taken.contains(it)) "복용 완료" else "미복용"}"
            }

            binding.tvTodayStatus.text = result
        }

        // 기존 기능들
        binding.btnAddLog.setOnClickListener {
            startActivity(Intent(requireContext(), LogActivity::class.java))
        }

        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(requireContext(), LogHistoryActivity::class.java))
        }

        binding.btnRecordMood.setOnClickListener {
            startActivity(Intent(requireContext(), RecordActivity::class.java))
        }

        binding.btnViewMoodDetails.setOnClickListener {
            val nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            nav?.selectedItemId = R.id.navigation_mood
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
