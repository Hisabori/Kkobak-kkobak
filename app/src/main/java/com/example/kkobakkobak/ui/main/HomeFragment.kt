package com.example.kkobakkobak.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import com.example.kkobakkobak.databinding.FragmentHomeBinding
import com.example.kkobakkobak.ui.main.MainActivity // 이 줄 추가!
import com.example.kkobakkobak.ui.log.LogActivity
import com.example.kkobakkobak.ui.history.LogHistoryActivity
import com.example.kkobakkobak.ui.record.RecordActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddLog.setOnClickListener {
            startActivity(Intent(requireContext(), LogActivity::class.java))
        }

        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(requireContext(), LogHistoryActivity::class.java))
        }

        binding.btnRecordMood.setOnClickListener {
            startActivity(Intent(requireContext(), RecordActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}