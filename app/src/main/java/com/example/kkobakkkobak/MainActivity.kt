package com.example.kkobakkkobak

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
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
        setupBlurView()

        prefs = getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        tvStreak = findViewById(R.id.tv_streak)

        initView("morning", findViewById(R.id.tv_morning_time), findViewById(R.id.btn_morning_set), findViewById(R.id.btn_morning_cancel))
        initView("lunch", findViewById(R.id.tv_lunch_time), findViewById(R.id.btn_lunch_set), findViewById(R.id.btn_lunch_cancel))
        initView("dinner", findViewById(R.id.tv_dinner_time), findViewById(R.id.btn_dinner_set), findViewById(R.id.btn_dinner_cancel))
        initView("bedtime", findViewById(R.id.tv_bedtime_time), findViewById(R.id.btn_bedtime_set), findViewById(R.id.btn_bedtime_cancel))

        setupStreakUpdateReceiver()

        findViewById<Button>(R.id.btn_add_log).setOnClickListener {
            startActivity(Intent(this, LogActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateStreakView()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 앱이 종료될 때 리시버 등록 해제 (메모리 누수 방지)
        streakUpdateReceiver?.let {
            unregisterReceiver(it)
        }
    }

    private fun initView(category: String, timeTextView: TextView, setButton: Button, cancelButton: Button) {
        updateTextView(category, timeTextView)
        setButton.setOnClickListener {
            showTimePickerDialog(category, timeTextView)
        }
        cancelButton.setOnClickListener {
            cancelAlarm(category)
            updateTextView(category, timeTextView)
            Toast.makeText(this, "${getCategoryKorean(category)} 알람이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimePickerDialog(category: String, timeTextView: TextView) {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            prefs.edit().apply {
                putInt("${category}_hour", hourOfDay)
                putInt("${category}_minute", minute)
                apply()
            }
            setAlarm(category, hourOfDay, minute)
            updateTextView(category, timeTextView)
        }
        TimePickerDialog(this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun updateTextView(category: String, timeTextView: TextView) {
        val hour = prefs.getInt("${category}_hour", -1)
        val minute = prefs.getInt("${category}_minute", -1)
        timeTextView.text = if (hour != -1 && minute != -1) {
            String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        } else {
            "설정되지 않음"
        }
    }

    private fun setAlarm(category: String, hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Toast.makeText(this, "${getCategoryKorean(category)} 알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "정확한 알람 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
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
    }

    private fun updateStreakView() {
        val lastTakenDate = prefs.getString("lastTakenDate", null)
        var streak = prefs.getInt("streak", 0)

        if (lastTakenDate != null && lastTakenDate != getTodayDateString() && lastTakenDate != getYesterdayDateString()) {
            streak = 0
            prefs.edit().putInt("streak", streak).apply()
        }

        if (streak > 0) {
            tvStreak.text = "🔥 $streak" + "일 연속 달성!"
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
        val intentFilter = IntentFilter("UPDATE_STREAK_ACTION")
        ContextCompat.registerReceiver(this, streakUpdateReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    private fun setupBlurView() {
        val radius = 15f
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background
        val blurView = findViewById<BlurView>(R.id.blurView)

        blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)
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