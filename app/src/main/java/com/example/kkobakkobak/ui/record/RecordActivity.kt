package com.example.kkobakkobak.ui.record

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kkobakkobak.R
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.databinding.ActivityRecordBinding
import com.example.kkobakkobak.ui.base.BaseActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordActivity : BaseActivity() {
    private lateinit var binding: ActivityRecordBinding
    private var selectedMood = 0
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbarLayout.toolbar, "기록하기", true)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getDatabase(this)

        binding.btnMoodGood.setOnClickListener { selectMood(1, binding.btnMoodGood) }
        binding.btnMoodNeutral.setOnClickListener { selectMood(2, binding.btnMoodNeutral) }
        binding.btnMoodBad.setOnClickListener { selectMood(3, binding.btnMoodBad) }

        binding.btnSaveRecord.setOnClickListener {
            if (selectedMood == 0) {
                Toast.makeText(this, "오늘의 기분을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val reason = binding.etReason.text.toString()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val log = MedicationLog(date = today, mood = selectedMood, memo = reason)

            lifecycleScope.launch {
                db.medicationLogDao().insert(log)
                Toast.makeText(applicationContext, "기분이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun selectMood(mood: Int, button: ImageButton) {
        selectedMood = mood
        binding.btnMoodGood.alpha = 0.5f
        binding.btnMoodNeutral.alpha = 0.5f
        binding.btnMoodBad.alpha = 0.5f
        button.alpha = 1.0f
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}