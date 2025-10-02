// ui/main/MainViewModel.kt 파일

package com.example.kkobakkobak.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    // 스트릭 업데이트 이벤트는 한 번만 소비되는 이벤트이므로 SharedFlow를 사용
    private val _streakUpdateEvent = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )
    val streakUpdateEvent: SharedFlow<Unit> = _streakUpdateEvent.asSharedFlow()

    // LogFragment에서 스트릭을 업데이트해야 할 때, 이 함수를 호출
    fun triggerStreakUpdate() {
        viewModelScope.launch {
            _streakUpdateEvent.emit(Unit)
        }
    }
}