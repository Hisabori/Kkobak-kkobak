package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.kkobakkobak.alarm.AlarmScheduler
import com.example.kkobakkobak.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull // Flow에서 데이터를 한 번만 가져오기 위한 import
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 부팅 완료 및 잠금 해제 부팅 완료 액션만 처리
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device reboot detected. Rescheduling alarms...")

            // SharedPreferences 로직 제거

            val db = AppDatabase.getDatabase(context)
            val scheduler = AlarmScheduler(context)
            // 백그라운드에서 DB 작업을 수행하기 위해 CoroutineScope 사용
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                try {
                    // Room의 Flow에서 활성화된 리마인더 목록을 가져옴
                    // DAO의 getAllReminders()는 Flow를 반환하므로, firstOrNull()로 List를 한 번만 가져옴
                    val reminders = db.medicationIntakeDao().getAllReminders().firstOrNull() ?: emptyList()

                    reminders.filter { it.isActive }.forEach { reminder ->
                        // 🚨 새로운 schedule(reminder) 함수 사용
                        scheduler.schedule(reminder)
                        Log.d("BootReceiver", "Rescheduled alarm for ${reminder.category} (ID: ${reminder.id})")
                    }
                } catch (e: Exception) {
                    // DB 접근 또는 알람 스케줄링 중 발생할 수 있는 오류를 로깅
                    Log.e("BootReceiver", "Error rescheduling alarms: ${e.message}")
                }
            }
        }
    }
}