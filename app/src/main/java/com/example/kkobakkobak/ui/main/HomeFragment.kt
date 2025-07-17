package com.example.kkobakkobak.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import com.example.kkobakkobak.databinding.FragmentHomeBinding
import com.example.kkobakkobak.ui.log.LogActivity
import com.example.kkobakkobak.ui.history.LogHistoryActivity
import com.example.kkobakkobak.ui.record.RecordActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.kkobakkobak.R

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
