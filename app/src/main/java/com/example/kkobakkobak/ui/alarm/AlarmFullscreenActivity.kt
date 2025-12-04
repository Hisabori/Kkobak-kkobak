// app/src/main/java/com/example/kkobakkobak/ui/alarm/AlarmFullscreenActivity.kt
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
import com.example.kkobakkobak.data.database.AppDatabase
import com.example.kkobakkobak.databinding.ActivityAlarmFullscreenBinding // 👈 Binding 클래스가 자동 생성되었다고 가정
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmFullscreenActivity : Activity() {

    private lateinit var binding: ActivityAlarmFullscreenBinding
    private val scope = CoroutineScope(Dispatchers.IO)
    private val SNOOZE_MINUTES = 15

    // 🔔 스와이프 로직 변수
    private lateinit var gestureDetector: GestureDetectorCompat
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔔 [블러 추가] 뒷 배경 블러 효과 적용 (API 31/S 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurRadius = 50
            window.attributes = window.attributes.apply {
                this.blurBehindRadius = blurRadius
            }
        }

        // 뷰 바인딩 초기화
        binding = ActivityAlarmFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 화면 켜짐 및 잠금 해제 처리
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val reminderId = intent.getIntExtra("REMINDER_ID", -1)
        val category = intent.getStringExtra("CATEGORY") ?: "복약"
        val medName = intent.getStringExtra("MEDICATION_NAME") ?: "약물"

        if (reminderId == -1) {
            Toast.makeText(this, "알람 정보가 유효하지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // UI 텍스트 설정
        binding.alarmTitle.text = "${category} 복약 시간입니다!"
        binding.alarmMessage.text = "약물: ${medName}"

        // 🔔 스와이프 로직 초기화
        gestureDetector = GestureDetectorCompat(this, SwipeGestureListener(reminderId))

        // slide_container에 터치 리스너 연결
        binding.slideContainer.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    // 🔔 스와이프 리스너 클래스
    private inner class SwipeGestureListener(private val reminderId: Int) : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = e2.x - (e1?.x ?: e2.x)
            val diffY = e2.y - (e1?.y ?: e2.y)

            // 수평 스와이프를 우선적으로 감지
            if (Math.abs(diffX) > Math.abs(diffY)) {

                // 충분히 멀리, 그리고 빠르게 움직였을 때
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                    if (diffX > 0) {
                        // ➡️ 우측으로 스와이프: 해제/복용 완료
                        completeMedication(reminderId)
                    } else {
                        // ⬅️ 좌측으로 스와이프: 15분 뒤 알람 (Snooze)
                        snoozeAlarm(reminderId, SNOOZE_MINUTES)
                    }
                    return true
                }
            }
            return false
        }
    }


    // 복용 완료 처리 로직 (우측 스와이프)
    private fun completeMedication(reminderId: Int) {
        val db = AppDatabase.getDatabase(applicationContext)

        scope.launch {
            val reminder = db.medicationIntakeDao().getReminderById(reminderId)
            if (reminder != null) {
                // Todo: 1. 복용 로그 기록 로직 완성 (MedicationLogDao.insert 등을 사용)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlarmFullscreenActivity, "복용 완료! 💪", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlarmFullscreenActivity, "알람 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    // 15분 뒤 알람 재설정 (스누즈) 로직 (좌측 스와이프)
    private fun snoozeAlarm(reminderId: Int, minutes: Int) {
        val db = AppDatabase.getDatabase(applicationContext)
        val scheduler = AlarmScheduler(applicationContext)

        scope.launch {
            val reminder = db.medicationIntakeDao().getReminderById(reminderId)
            if (reminder != null) {
                scheduler.scheduleSnooze(reminder, minutes)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlarmFullscreenActivity, "${minutes}분 뒤 다시 알릴게.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AlarmFullscreenActivity, "알람 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}