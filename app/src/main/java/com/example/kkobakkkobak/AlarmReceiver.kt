package com.example.kkobakkkobak

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val category = intent.getStringExtra("category") ?: run {
            Log.e("AlarmReceiver", "Category is null, cannot process alarm.")
            return // category가 없으면 알람 처리 중단
        }
        val medName = intent.getStringExtra("medName")
        // onReceive에서 어떤 약 이름이 수신되는지 확인하는 로그
        Log.d("AlarmReceiver", "onReceive triggered for category: $category, received medName: '$medName'")
        sendNotification(context, category, medName)
        rescheduleAlarm(context, category, medName)
    }

    private fun sendNotification(context: Context, category: String, medName: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_alarm_channel"
        val notificationId = getRequestCode(category)

        // Android 8.0 (API 26) 이상에서는 알림 채널을 생성해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "투약 알림", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 클릭 시 실행될 MainActivity의 Intent 및 PendingIntent
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 새로운 태스크로 시작하고 기존 태스크를 지웁니다.
        }
        val pendingIntent = PendingIntent.getActivity(context, notificationId, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // '먹었어요' 버튼 클릭 시 실행될 BroadcastReceiver의 Intent 및 PendingIntent
        val takenIntent = Intent(context, MedicationTakenReceiver::class.java).apply {
            putExtra("notificationId", notificationId) // 알림 ID를 전달하여 클릭 시 해당 알림을 제거할 수 있도록 합니다.
            // 필요하다면 여기에 약 이름 등 추가 데이터를 putExtra 할 수 있습니다.
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId, // 고유한 request code 사용
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 제목과 메시지 내용 생성
        val (title, message) = getNotificationContent(context, category, medName)

        // NotificationCompat.Builder를 사용하여 알림 생성
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_medication) // 알림 아이콘
            .setContentTitle(title) // 알림 제목
            .setContentText(message) // 알림 내용
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 높은 우선순위로 설정하여 헤드업 알림으로 표시될 가능성 높임
            .setContentIntent(pendingIntent) // 알림 클릭 시 실행될 PendingIntent
            .setAutoCancel(true) // 알림 클릭 시 자동으로 사라지게 함
            .addAction(R.drawable.ic_check, "먹었어요 ✅", takenPendingIntent) // '먹었어요' 버튼 추가

        // 알림을 표시
        notificationManager.notify(notificationId, builder.build())
    }

    // 알람을 재설정하는 함수 (매일 같은 시간에 반복되도록)
    private fun rescheduleAlarm(context: Context, category: String, medName: String?) {
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val hour = prefs.getInt("${category}_hour", -1)
        val minute = prefs.getInt("${category}_minute", -1)

        // 알람 시간이 설정되어 있지 않으면 재설정하지 않음
        if (hour == -1 || minute == -1) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

        // 재설정할 알람에도 동일한 카테고리와 약 이름 데이터를 전달
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("category", category)
            putExtra("medName", medName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, getRequestCode(category), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 다음 날 같은 시간으로 Calendar 설정
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            add(Calendar.DATE, 1) // 현재 시간보다 이전이면 다음 날로 설정되어야 하므로 무조건 1일 추가
        }

        try {
            // 정확한 시간에 알람 설정
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            Log.d("AlarmReceiver", "Rescheduled alarm for $category at ${hour}:${minute}, medName: '$medName'")
        } catch (e: SecurityException) {
            // 권한 문제 발생 시 로그 기록 (앱 시작 시 권한 요청이 필요)
            Log.e("AlarmReceiver", "Failed to reschedule alarm due to security exception: ${e.message}")
        }
    }

    // 알람 카테고리별 고유한 RequestCode를 반환하는 함수
    private fun getRequestCode(category: String): Int = when (category) {
        "morning" -> 101
        "lunch" -> 102
        "dinner" -> 103
        "bedtime" -> 104
        else -> 0
    }

    // 알림에 표시될 제목과 메시지 내용을 반환하는 함수
    private fun getNotificationContent(context: Context, category: String, medName: String?): Pair<String, String> {
        // 알림 제목은 카테고리에 따라 다르게 설정
        val title = when (category) {
            "morning" -> "아침 약 ☀️"
            "lunch" -> "점심 약 🍚"
            "dinner" -> "저녁 약 🌙"
            "bedtime" -> "취침 전 약 🛏️"
            else -> "투약 시간"
        }

        // 'encouragement_messages'는 res/values/strings.xml에 정의되어 있어야 합니다.
        // 예: <string-array name="encouragement_messages"><item>잊지 말고 꼭 챙겨 드세요!</item></string-array>
        val messages = context.resources.getStringArray(R.array.encouragement_messages)
        var randomMessage = messages.random() // 랜덤 격려 메시지 선택

        // 약 이름이 null이 아니거나 비어있지 않고, '미설정' 문자열이 아닌 경우에만 메시지에 약 이름을 추가
        if (!medName.isNullOrEmpty() && medName != "미설정") {
            randomMessage = "$medName, $randomMessage"
            Log.d("AlarmReceiver", "Notification content with medName: '$medName', final message: '$randomMessage'")
        } else {
            // 약 이름이 없거나 '미설정'일 경우 경고 로그
            Log.w("AlarmReceiver", "Medication name is null or empty or '미설정' for category: $category. Displaying only random message: '$randomMessage'")
        }

        return title to randomMessage
    }
}