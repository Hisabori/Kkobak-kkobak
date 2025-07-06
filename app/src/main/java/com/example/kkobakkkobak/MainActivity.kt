package com.example.kkobakkkobak

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var tvStreak: TextView
    private var streakUpdateReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestNotificationPermission()

        prefs = getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        tvStreak = findViewById(R.id.tv_streak)

        // 각 항목 뷰 설정 (ID를 고유하게 변경했습니다.)
        setupReminderView("morning", "아침", "☀️", findViewById(R.id.item_morning_alarm_layout))
        setupReminderView("lunch", "점심", "🍚", findViewById(R.id.item_lunch_alarm_layout))
        setupReminderView("dinner", "저녁", "🌙", findViewById(R.id.item_dinner_alarm_layout))
        setupReminderView("bedtime", "취침 전", "🛏️", findViewById(R.id.item_bedtime_alarm_layout))

        setupStreakUpdateReceiver()

        findViewById<Button>(R.id.btn_add_log).setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }

        findViewById<Button>(R.id.btn_view_history).setOnClickListener {
            startActivity(Intent(this, LogHistoryActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateStreakView()
        updateAllReminderViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        streakUpdateReceiver?.let {
            unregisterReceiver(it)
        }
    }

    private fun updateAllReminderViews() {
        setupReminderView("morning", "아침", "☀️", findViewById(R.id.item_morning_alarm_layout))
        setupReminderView("lunch", "점심", "🍚", findViewById(R.id.item_lunch_alarm_layout))
        setupReminderView("dinner", "저녁", "🌙", findViewById(R.id.item_dinner_alarm_layout))
        setupReminderView("bedtime", "취침 전", "🛏️", findViewById(R.id.item_bedtime_alarm_layout))
    }

    private fun setupReminderView(
        category: String,
        title: String,
        iconEmoji: String,
        itemView: View // 개별 알람 항목의 부모 뷰
    ) {
        // 각 카테고리에 맞는 고유한 ID를 사용하여 뷰를 찾습니다.
        val icon: TextView = itemView.findViewById(resources.getIdentifier("tv_category_icon_${category}", "id", packageName))
        val titleTextView: TextView = itemView.findViewById(resources.getIdentifier("tv_category_title_${category}", "id", packageName))
        val timeTextView: TextView = itemView.findViewById(resources.getIdentifier("tv_time_${category}", "id", packageName))
        val setButton: Button = itemView.findViewById(resources.getIdentifier("btn_set_${category}", "id", packageName))
        val medNameTextView: TextView = itemView.findViewById(resources.getIdentifier("tv_med_name_${category}", "id", packageName))
        val setMedNameButton: Button = itemView.findViewById(resources.getIdentifier("btn_set_med_name_${category}", "id", packageName))


        icon.text = iconEmoji
        titleTextView.text = title

        val hour = prefs.getInt("${category}_hour", -1)
        val minute = prefs.getInt("${category}_minute", -1)
        val savedMedName = prefs.getString("${category}_med_name", "미설정")

        if (hour != -1 && minute != -1) {
            val amPm = if (hour < 12) "오전" else "오후"
            val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
            timeTextView.text =
                String.format(Locale.KOREA, "%s %02d:%02d", amPm, displayHour, minute)
            setButton.text = "취소"

            setButton.setOnClickListener {
                cancelAlarm(category)
                updateAllReminderViews()
            }
        } else {
            timeTextView.text = "설정되지 않음"
            setButton.text = "설정"

            setButton.setOnClickListener {
                showTimePickerDialog(category)
            }
        }

        medNameTextView.text = savedMedName
        setMedNameButton.setOnClickListener {
            showMedicationNameInputDialog(category, savedMedName)
        }
    }

    // 약 이름을 입력받는 AlertDialog를 띄우는 함수
    private fun showMedicationNameInputDialog(category: String, currentMedName: String?) {
        val inputEditText = EditText(this@MainActivity).apply {
            inputType = InputType.TYPE_CLASS_TEXT // 텍스트 입력 유형 설정
            hint = "약 이름을 입력하세요 (예: 웰부트린, 콘서타)"
            setText(currentMedName.takeIf { it != "미설정" } ?: "") // 현재 값이 '미설정'이 아니면 EditText에 미리 채움
        }

        AlertDialog.Builder(this)
            .setTitle("${getCategoryKorean(category)} 약 이름 설정")
            .setView(inputEditText) // EditText를 다이얼로그에 추가
            .setPositiveButton("저장") { dialog, _ ->
                val newMedName = inputEditText.text.toString().trim() // 입력된 약 이름 가져오기
                prefs.edit().apply {
                    putString("${category}_med_name", newMedName.ifEmpty { "미설정" }) // 빈 값이면 "미설정"으로 저장
                    apply() // SharedPreferences에 적용
                }
                // 약 이름 변경 후, 해당 카테고리의 알람이 이미 설정되어 있다면 새 약 이름으로 알람을 재설정하여 반영
                val hour = prefs.getInt("${category}_hour", -1)
                val minute = prefs.getInt("${category}_minute", -1)
                if (hour != -1 && minute != -1) {
                    setAlarm(category, hour, minute, newMedName.ifEmpty { "미설정" })
                }
                updateAllReminderViews() // UI를 다시 그려서 변경된 약 이름 반영

                // 약이름 설정 완료 토스트 메시지 추가
                Toast.makeText(this, "약이름 설정 완료!", Toast.LENGTH_SHORT).show()

                dialog.dismiss() // 다이얼로그 닫기
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel() // 다이얼로그 취소
            }
            .show() // 다이얼로그 표시
    }

    private fun showTimePickerDialog(category: String) {
        val calendar = Calendar.getInstance() // 현재 시간을 기준으로 Calendar 인스턴스 생성
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            // 선택된 시간을 SharedPreferences에 저장
            prefs.edit().apply {
                putInt("${category}_hour", hourOfDay)
                putInt("${category}_minute", minute)
                apply()
            }

            // 저장된 약 이름을 SharedPreferences에서 가져옴 (사용자가 입력한 값)
            val medName = prefs.getString("${category}_med_name", "미설정")

            // 알람 설정 함수 호출
            setAlarm(category, hourOfDay, minute, medName)
            updateAllReminderViews() // UI 업데이트

            // 알람 설정 완료 메시지를 보여주는 CompletionActivity로 이동 (필요하다면)
            val intent = Intent(this, CompletionActivity::class.java).apply {
                putExtra("message", "알람 설정 완료!")
            }
            startActivity(intent)
        }
        // TimePickerDialog 표시
        TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 24시간 형식이 아닌 AM/PM 형식 (true로 하면 24시간 형식)
        ).show()
    }

    // 실제 알람을 설정하는 함수
    private fun setAlarm(category: String, hour: Int, minute: Int, medName: String?) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12 (API 31) 이상에서 정확한 알람 설정 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // 권한이 없으면 설정 화면으로 이동 요청
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
                Toast.makeText(this, "알람 설정을 위해 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
                return // 권한이 없으므로 함수 종료
            }
        }

        // 알람 설정을 위한 로그 (어떤 약 이름이 전달되는지 확인)
        Log.d("MainActivity", "Setting alarm for category: $category, medName: '$medName'")

        // AlarmReceiver로 보낼 Intent 생성 및 데이터 추가
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("category", category)
            putExtra("medName", medName) // 약 이름 데이터를 Intent에 추가
        }

        // PendingIntent 생성: BroadcastReceiver를 시작하고, 기존 PendingIntent가 있다면 취소 후 새로 생성
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            getRequestCode(category), // 각 알람 카테고리별 고유한 RequestCode 사용
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE // 기존 PendingIntent 취소 후 새로 생성, 변경 불가 플래그
        )

        // 알람이 울릴 시간을 설정 (Calendar 객체 사용)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis() // 현재 시간으로 초기화
            set(Calendar.HOUR_OF_DAY, hour) // 설정된 시
            set(Calendar.MINUTE, minute) // 설정된 분
            set(Calendar.SECOND, 0) // 초는 0으로 초기화
            // 만약 설정 시간이 현재 시간보다 이전이면 다음 날로 설정 (매일 반복 알람을 위함)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // 정확한 시간에 알람 설정 (앱이 Doze 모드일 때도 작동)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Log.d("MainActivity", "Alarm set for $category at ${hour}:${minute}")
    }

    // 알람을 취소하는 함수
    private fun cancelAlarm(category: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        // PendingIntent.FLAG_NO_CREATE: 기존 PendingIntent가 없으면 새로 생성하지 않음 (취소 목적)
        val pendingIntent = PendingIntent.getBroadcast(
            this, getRequestCode(category), intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        // PendingIntent가 존재하면 알람 취소 및 PendingIntent도 취소
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d("MainActivity", "Cancelled alarm for $category")
        }
        // SharedPreferences에서 저장된 알람 시간과 약 이름 삭제
        prefs.edit().apply {
            remove("${category}_hour")
            remove("${category}_minute")
            remove("${category}_med_name")
            apply()
        }
        Toast.makeText(this, "${getCategoryKorean(category)} 알람이 취소되었습니다.", Toast.LENGTH_SHORT).show()
    }

    // 연속 복용일(스트릭)을 업데이트하는 함수
    private fun updateStreakView() {
        val lastTakenDate = prefs.getString("lastTakenDate", null)
        var streak = prefs.getInt("streak", 0)

        // 마지막 복용일이 어제나 오늘이 아니면 스트릭 초기화
        if (lastTakenDate != null && lastTakenDate != getTodayDateString() && lastTakenDate != getYesterdayDateString()) {
            streak = 0
            prefs.edit().putInt("streak", streak).apply()
        }

        // 스트릭 값에 따라 TextView 업데이트
        if (streak > 0) {
            tvStreak.text = "🔥 $streak" + "일 연속으로 챙겼어요!"
        } else {
            tvStreak.text = "🔥 꾸준히 약을 챙겨보세요!"
        }
    }

    // 스트릭 업데이트를 위한 BroadcastReceiver를 설정하는 함수
    private fun setupStreakUpdateReceiver() {
        streakUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "UPDATE_STREAK_ACTION") {
                    updateStreakView()
                }
            }
        }
        // BroadcastReceiver 등록 (Android 12 이상에서 RECEIVER_NOT_EXPORTED 필요)
        ContextCompat.registerReceiver(this, streakUpdateReceiver, IntentFilter("UPDATE_STREAK_ACTION"), ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    // 오늘 날짜 문자열을 반환하는 헬퍼 함수
    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // 어제 날짜 문자열을 반환하는 헬퍼 함수
    private fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    // 각 알람 카테고리별 고유한 RequestCode를 반환하는 함수
    private fun getRequestCode(category: String): Int = when (category) {
        "morning" -> 101
        "lunch" -> 102
        "dinner" -> 103
        "bedtime" -> 104
        else -> 0
    }

    // 카테고리 영문명을 한글명으로 변환하는 헬퍼 함수
    private fun getCategoryKorean(category: String): String = when (category) {
        "morning" -> "아침"
        "lunch" -> "점심"
        "dinner" -> "저녁"
        "bedtime" -> "취침 전"
        else -> ""
    }

    // 알림 권한 요청을 위한 ActivityResultLauncher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_LONG).show()
        }

    // 알림 권한을 요청하는 함수
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}