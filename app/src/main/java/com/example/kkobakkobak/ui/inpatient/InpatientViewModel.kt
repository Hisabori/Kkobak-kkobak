package com.example.kkobakkobak.ui.inpatient

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kkobakkobak.data.database.InpatientEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InpatientViewModel(application: Application) : AndroidViewModel(application) {

    private val _items = MutableStateFlow<List<InpatientEntity>>(emptyList())
    val items = _items.asStateFlow()

    fun load() {
        viewModelScope.launch {
            // 나중에 여기에 데이터를 불러오는 로직을 넣으면 돼
        }
    }
}