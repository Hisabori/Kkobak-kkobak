package com.example.kkobakkobak.ui.record

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.data.model.MoodLog
import com.example.kkobakkobak.databinding.ActivityRecordBinding
import com.example.kkobakkobak.ui.base.BaseActivity
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch
import java.util.Date

class RecordActivity : BaseActivity() {
    private lateinit var binding: ActivityRecordBinding
    private var currentMoodValue = 50 // Default value
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbarLayout.toolbar, "기록하기", true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getDatabase(this)

        binding.moodSlider.addOnChangeListener { _, value, _ ->
            currentMoodValue = value.toInt()
            binding.tvSliderValue.text = currentMoodValue.toString()
        }

        // Set initial value
        binding.tvSliderValue.text = currentMoodValue.toString()

        binding.btnSaveRecord.setOnClickListener {
            val reason = binding.etReason.text.toString()
            val log = MoodLog(mood = currentMoodValue, content = reason, date = System.currentTimeMillis())

            lifecycleScope.launch {
                db.moodDao().insertMoodLog(log)
                Toast.makeText(applicationContext, "기분이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
