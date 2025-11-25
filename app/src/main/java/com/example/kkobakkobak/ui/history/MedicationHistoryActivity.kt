package com.example.kkobakkobak.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kkobakkobak.databinding.ActivityMedicationHistoryBinding

class MedicationHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedicationHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 뷰 바인딩 연결
        binding = ActivityMedicationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. 기본 UI 설정
        binding.rvMedicationHistory.layoutManager = LinearLayoutManager(this)

        // TODO: 나중에 여기에 기록을 보여줄 어댑터(Adapter)를 연결해야 해.
        // 지금은 화면이 뜨는 것까지만 성공시켜보자!
    }
}