package com.example.kkobakkkobak

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
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

        // 각 항목 뷰 설정 (아이콘 리소스 대신 이모지 문자열 전달)
        setupReminderView("morning", "아침", "☀️", findViewById(R.id.item_morning))
        setupReminderView("lunch", "점심", "🍚", findViewById(R.id.item_lunch))
        setupReminderView("dinner", "저녁", "🌙", findViewById(R.id.item_dinner))
        setupReminderView("bedtime", "취침 전", "🛏️", findViewById(R.id.item_bedtime))

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
        setupReminderView("morning", "아침", "☀️", findViewById(R.id.item_morning))
        setupReminderView("lunch", "점심", "🍚", findViewById(R.id.item_lunch))
        setupReminderView("dinner", "저녁", "🌙", findViewById(R.id.item_dinner))
        setupReminderView("bedtime", "취침 전", "🛏️", findViewById(R.id.item_bedtime))
    }

    // 파라미터 타입을 String으로 변경하고, ImageView를 TextView로 변경
    private fun setupReminderView(category: String, title: String, iconEmoji: String, itemView: View) {
        val icon: TextView = itemView.findViewById(R.id.tv_category_icon)
        val titleTextView: TextView = itemView.findViewById(R.id.tv_category_title)
        val timeTextView: TextView = itemView.findViewById(R.id.tv_time)
        val setButton: Button = itemView.findViewById(R.id.btn_set)

        icon.text = iconEmoji // 이모지 설정
        titleTextView.text = title

        val hour = prefs.getInt("${category}_hour", -1)
        val minute = prefs.getInt("${category}_minute", -1)

        if (hour != -1 && minute != -1) {
            val amPm = if (hour < 12) "오전" else "오후"
            val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
            timeTextView.text = String.format(Locale.KOREA, "%s %02d:%02d", amPm, displayHour, minute)
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
    }

    private fun showTimePickerDialog(category: String) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            prefs.edit().apply {
                putInt("${category}_hour", hourOfDay)
                putInt("${category}_minute", minute)
                apply()
            }
            setAlarm(category, hourOfDay, minute)
            updateAllReminderViews()

            val intent = Intent(this, CompletionActivity::class.java).apply {
                putExtra("message", "알람 설정 완료!")
            }
            startActivity(intent)
        }
        TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
    }

    private fun setAlarm(category: String, hour: Int, minute: Int) {
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

        val intent = Intent(this, AlarmReceiver::class.java).putExtra("category", category)
        val pendingIntent = PendingIntent.getBroadcast(
            this, getRequestCode(category), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
        }
        prefs.edit().apply {
            remove("${category}_hour")
            remove("${category}_minute")
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
        "morning" -> 101; "lunch" -> 102; "dinner" -> 103; "bedtime" -> 104; else -> 0
    }

    private fun getCategoryKorean(category: String): String = when (category) {
        "morning" -> "아침"; "lunch" -> "점심"; "dinner" -> "저녁"; "bedtime" -> "취침 전"; else -> ""
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
