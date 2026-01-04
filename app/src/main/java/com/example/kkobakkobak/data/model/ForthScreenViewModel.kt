package com.example.kkobakkobak.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kkobakkobak.network.MyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FourthScreenUiState(
    val isLoading: Boolean = false,
    val institutions: List<Row> = emptyList(),
    val error: String? = null
)

class ForthScreenViewModel(
    private val repository: MyRepository = MyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FourthScreenUiState())
    val uiState: StateFlow<FourthScreenUiState> = _uiState.asStateFlow()

    init { loadInstitutions() }

    private fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.fetchData()
                _uiState.update { it.copy(isLoading = false, institutions = response) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}