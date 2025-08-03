package com.example.kkobakkobak.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import kotlinx.coroutines.launch

class LogHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_history)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = AppDatabase.getDatabase(this)
        val recyclerView: RecyclerView = findViewById(R.id.rv_log_history)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val logs = db.medicationIntakeDao().getAllIntakes() // ✅ 여기 핵심
            recyclerView.adapter = LogHistoryAdapter(logs)
        }
    }
}
