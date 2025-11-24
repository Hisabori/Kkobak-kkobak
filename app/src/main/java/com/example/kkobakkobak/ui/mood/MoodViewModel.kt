package com.example.kkobakkobak.ui.mood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kkobakkobak.data.model.MoodLog
import kotlinx.coroutines.launch

class MoodViewModel : ViewModel() {

    private val _moodLogs = MutableLiveData<List<MoodLog>>()
    val moodLogs: LiveData<List<MoodLog>> = _moodLogs

    fun fetchMoodLogs() {
        viewModelScope.launch {
            // TODO: Implement your data fetching logic here (e.g., from a repository)
            // For now, we\'ll use dummy data
            _moodLogs.value = getDummyMoodLogs()
        }
    }

    private fun getDummyMoodLogs(): List<MoodLog> {
        // Replace with your actual data source
        return listOf(
            MoodLog(mood = 5, content = "Had a great day!", date = System.currentTimeMillis()),
            MoodLog(mood = 2, content = "Feeling a bit down.", date = System.currentTimeMillis() - 86400000),
            MoodLog(mood = 3, content = "Just a regular day.", date = System.currentTimeMillis() - 172800000)
        )
    }
}
