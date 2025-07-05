package com.example.kkobakkkobak

import android.os.Bundle
import android.view.MenuItem // 이 import 문을 추가하세요.
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class LogHistoryActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_history)

        // --- 아래 코드를 추가하여 뒤로 가기 버튼을 표시합니다 ---
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // ---------------------------------------------

        db = AppDatabase.getDatabase(this)
        val recyclerView: RecyclerView = findViewById(R.id.rv_log_history)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val logs = db.medicationLogDao().getAllLogs()
            recyclerView.adapter = LogHistoryAdapter(logs)
        }
    }

    // --- 아래 함수를 클래스 안에 추가하여 뒤로 가기 버튼 동작을 처리합니다 ---
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // 현재 액티비티를 닫고 이전 화면으로 돌아감
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    // ---------------------------------------------------------
}