package com.example.kkobakkobak.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kkobakkobak.data.repo.MedicationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// 생성자에서 MedicationRepository를 주입받도록 수정 (필요 시)
class MainViewModel(
    private val medicationRepository: MedicationRepository? = null
) : ViewModel() {

    private val _streakUpdateEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )
    val streakUpdateEvent: SharedFlow<Unit> = _streakUpdateEvent.asSharedFlow()

    fun triggerStreakUpdate() {
        viewModelScope.launch {
            _streakUpdateEvent.emit(Unit)
        }
    }

    // 에러가 났던 부분: 함수 호출 전 repository 존재 여부와 함수명 확인
    fun syncData() {
        viewModelScope.launch {
            try {
                medicationRepository?.syncAllMedicationLogs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}