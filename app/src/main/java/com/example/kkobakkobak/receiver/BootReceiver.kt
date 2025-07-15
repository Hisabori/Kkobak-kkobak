package com.example.kkobakkobak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.kkobakkobak.alarm.AlarmScheduler // 알람 스케줄러
import com.example.kkobakkobak.receiver.AlarmReceiver // AlarmReceiver (필요하다면)

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 부팅 완료 시 알람 재설정 로직
            val alarmScheduler = AlarmScheduler(context)
            // 모든 기존 알람을 다시 스케줄링해야 합니다.
            // 여기서는 간단히 예시로 모든 알람을 재설정하는 코드를 넣지만,
            // 실제 앱에서는 저장된 알람 목록을 불러와서 각각 재설정해야 합니다.
            // 예를 들어, SharedPreferences나 데이터베이스에서 알람 정보를 불러와서 재설정.

            // AlarmScheduler에 모든 알람을 재설정하는 메서드를 추가하는 것이 더 효율적입니다.
            // 현재 AlarmScheduler에 개별 스케줄링만 있으므로, 여기서는 예시를 생략합니다.
            // 실제 구현에서는 앱의 모든 알람 데이터를 불러와서 for 문 등으로 재스케줄링해야 합니다.
        }
    }
}
