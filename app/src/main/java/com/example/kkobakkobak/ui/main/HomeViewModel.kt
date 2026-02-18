package com.example.kkobakkobak.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kkobakkobak.data.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    
    private val _todayIntakeCount = MutableStateFlow(0)
    val todayIntakeCount: StateFlow<Int> = _todayIntakeCount.asStateFlow()

    fun loadTodayIntake() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val intakes = db.medicationIntakeDao().getTodayIntakeList(today)
            _todayIntakeCount.value = intakes.size
        }
    }
}
