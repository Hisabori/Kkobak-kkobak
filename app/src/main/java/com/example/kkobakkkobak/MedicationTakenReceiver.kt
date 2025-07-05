package com.example.kkobakkkobak

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

class MedicationTakenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val notificationId = intent.getIntExtra("notificationId", -1)
        if (notificationId != -1) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }

        val today = getTodayDateString()
        val lastTakenDate = prefs.getString("lastTakenDate", null)
        var streak = prefs.getInt("streak", 0)

        if (lastTakenDate != today) {
            val yesterday = getYesterdayDateString()
            if (lastTakenDate == yesterday) {
                streak++
            } else {
                streak = 1
            }
            editor.putString("lastTakenDate", today)
            editor.putInt("streak", streak)
            editor.apply()
        }

        // --- 스트릭 계산 로직 끝 ---

        // MainActivity에 스트릭을 업데이트하라는 신호를 보냄
        val updateIntent = Intent("UPDATE_STREAK_ACTION")
        context.sendBroadcast(updateIntent)

        // --- 새로 만든 완료 화면을 띄웁니다 ---
        val completionIntent = Intent(context, CompletionActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("message", "투약 완료!")
        }
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
}