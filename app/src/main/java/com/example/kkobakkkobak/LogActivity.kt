package com.example.kkobakkkobak

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LogActivity : AppCompatActivity() {

    private var selectedMood = 0
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getDatabase(this)

        val btnMoodGood: ImageButton = findViewById(R.id.btn_mood_good)
        val btnMoodNeutral: ImageButton = findViewById(R.id.btn_mood_neutral)
        val btnMoodBad: ImageButton = findViewById(R.id.btn_mood_bad)
        val btnSaveLog: Button = findViewById(R.id.btn_save_log)
        val etMemo: EditText = findViewById(R.id.et_memo)

        btnMoodGood.setOnClickListener { selectMood(1, btnMoodGood) }
        btnMoodNeutral.setOnClickListener { selectMood(2, btnMoodNeutral) }
        btnMoodBad.setOnClickListener { selectMood(3, btnMoodBad) }

        btnSaveLog.setOnClickListener {
            if (selectedMood == 0) {
                Toast.makeText(this, "오늘의 기분을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val memo = etMemo.text.toString()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val log = MedicationLog(date = today, mood = selectedMood, memo = memo)

            lifecycleScope.launch {
                db.medicationLogDao().insert(log)
                Toast.makeText(applicationContext, "오늘의 기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun selectMood(mood: Int, button: ImageButton) {
        selectedMood = mood
        findViewById<ImageButton>(R.id.btn_mood_good).alpha = 0.5f
        findViewById<ImageButton>(R.id.btn_mood_neutral).alpha = 0.5f
        findViewById<ImageButton>(R.id.btn_mood_bad).alpha = 0.5f
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