package com.example.kkobakkobak.ui.alarm

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.data.database.AppDatabase // 💡 base 대신 AppDatabase 임포트
import com.example.kkobakkobak.databinding.ActivityAlarmFullscreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmFullscreenActivity : Activity() {

    private lateinit var binding: ActivityAlarmFullscreenBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private val SNOOZE_MINUTES = 15

    private lateinit var gestureDetector: GestureDetectorCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.attributes.blurBehindRadius = 50
        }

        binding = ActivityAlarmFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // 💡 reminderId 타입을 Long에서 Int로 안전하게 변환 (image_2d36ca 에러 해결)
        val reminderId = intent.getLongExtra("REMINDER_ID", -1L).toInt()
        val category = intent.getStringExtra("CATEGORY") ?: "복약"
        val medName = intent.getStringExtra("MEDICATION_NAME") ?: "약물"

        binding.alarmTitle.text = "${category} 복약 시간입니다!"
        binding.alarmMessage.text = "약물: ${medName}"

        gestureDetector = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, vX: Float, vY: Float): Boolean {
                val diffX = e2.x - (e1?.x ?: e2.x)
                if (Math.abs(diffX) > 100 && Math.abs(vX) > 100) {
                    if (diffX > 0) completeMedication(reminderId)
                    else snoozeAlarm(reminderId, SNOOZE_MINUTES)
                    return true
                }
                return false
            }
        })

        binding.slideContainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun completeMedication(reminderId: Int) {
        val db = AppDatabase.getDatabase(applicationContext) // 💡 base -> AppDatabase (image_2d9fa3 에러 해결)
        scope.launch {
            val reminder = db.medicationIntakeDao().getReminderById(reminderId)
            withContext(Dispatchers.Main) {
                if (reminder != null) {
                    Toast.makeText(this@AlarmFullscreenActivity, "복용 완료! 💪", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun snoozeAlarm(reminderId: Int, minutes: Int) {
        val db = AppDatabase.getDatabase(applicationContext) // 💡 base -> AppDatabase
        val scheduler = AlarmScheduler(applicationContext)
        scope.launch {
            val reminder = db.medicationIntakeDao().getReminderById(reminderId)
            if (reminder != null) {
                scheduler.scheduleSnooze(reminder, minutes)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlarmFullscreenActivity, "${minutes}분 뒤 다시 알릴게요.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}