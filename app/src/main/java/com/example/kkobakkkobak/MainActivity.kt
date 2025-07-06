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
        // UI를 새로고침할 때도 변경된 ID로 뷰를 찾아야 합니다.
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

    private fun showMedicationNameInputDialog(category: String, currentMedName: String?) {
        val inputEditText = EditText(this@MainActivity).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = "약 이름을 입력하세요 (예: 웰부트린, 콘서타)"
            setText(currentMedName.takeIf { it != "미설정" } ?: "")
        }

        AlertDialog.Builder(this)
            .setTitle("${getCategoryKorean(category)} 약 이름 설정")
            .setView(inputEditText)
            .setPositiveButton("저장") { dialog, _ ->
                val newMedName = inputEditText.text.toString().trim()
                prefs.edit().apply {
                    putString("${category}_med_name", newMedName.ifEmpty { "미설정" })
                    apply()
                }
                val hour = prefs.getInt("${category}_hour", -1)
                val minute = prefs.getInt("${category}_minute", -1)
                if (hour != -1 && minute != -1) {
                    // 약 이름 변경 시에도 알람을 재설정하여 새로운 약 이름을 AlarmReceiver로 전달합니다.
                    setAlarm(category, hour, minute, newMedName.ifEmpty { "미설정" })
                }
                updateAllReminderViews()
                Toast.makeText(
                    this,
                    "${getCategoryKorean(category)} 약 이름이 저장되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun showTimePickerDialog(category: String) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            prefs.edit().apply {
                putInt("${category}_hour", hourOfDay)
                putInt("${category}_minute", minute)
                apply()
            }

            val medName = prefs.getString("${category}_med_name", "미설정")

            setAlarm(category, hourOfDay, minute, medName)
            updateAllReminderViews()

            val intent = Intent(this, CompletionActivity::class.java).apply {
                putExtra("message", "알람 설정 완료!")
            }
            startActivity(intent)
        }
        TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun setAlarm(category: String, hour: Int, minute: Int, medName: String?) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
                Toast.makeText(this, "알람 설정을 위해 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
                return
            }
        }

        Log.d("MainActivity", "Setting alarm for category: $category, medName: '$medName'")

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("category", category)
            putExtra("medName", medName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            getRequestCode(category),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Log.d("MainActivity", "Alarm set for $category at ${hour}:${minute}")
    }

    private fun cancelAlarm(category: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, getRequestCode(category), intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d("MainActivity", "Cancelled alarm for $category")
        }
        prefs.edit().apply {
            remove("${category}_hour")
            remove("${category}_minute")
            remove("${category}_med_name")
            apply()
        }
        Toast.makeText(this, "${getCategoryKorean(category)} 알람이 취소되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun updateStreakView() {
        val lastTakenDate = prefs.getString("lastTakenDate", null)
        var streak = prefs.getInt("streak", 0)

        if (lastTakenDate != null && lastTakenDate != getTodayDateString() && lastTakenDate != getYesterdayDateString()) {
            streak = 0
            prefs.edit().putInt("streak", streak).apply()
        }

        if (streak > 0) {
            tvStreak.text = "🔥 $streak" + "일 연속으로 챙겼어요!"
        } else {
            tvStreak.text = "🔥 꾸준히 약을 챙겨보세요!"
        }
    }

    private fun setupStreakUpdateReceiver() {
        streakUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "UPDATE_STREAK_ACTION") {
                    updateStreakView()
                }
            }
        }
        ContextCompat.registerReceiver(this, streakUpdateReceiver, IntentFilter("UPDATE_STREAK_ACTION"), ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun getRequestCode(category: String): Int = when (category) {
        "morning" -> 101
        "lunch" -> 102
        "dinner" -> 103
        "bedtime" -> 104
        else -> 0
    }

    private fun getCategoryKorean(category: String): String = when (category) {
        "morning" -> "아침"
        "lunch" -> "점심"
        "dinner" -> "저녁"
        "bedtime" -> "취침 전"
        else -> ""
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) Toast.makeText(this, "알림 권한이 거부되었습니다.", Toast.LENGTH_LONG).show()
        }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}