package com.example.kkobakkobak.ui.fourthscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kkobakkobak.network.MyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import com.example.kkobakkobak.data.model.Row

data class FourthScreenUiState(
    val isLoading: Boolean = false,
    val institutions: List<Row> = emptyList(),
    val error: String? = null
)

class FourthScreenViewModel(
    private val repository: MyRepository = MyRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FourthScreenUiState())
    val uiState: StateFlow<FourthScreenUiState> = _uiState.asStateFlow()

    init {
        loadInstitutions()
    }

    private fun loadInstitutions() {
        viewModelScope.launch {
            // 1. 로딩 상태 시작
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 2. 데이터 호출
                val response = repository.fetchData()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        institutions = response // fetchData()가 List<Row>를 반환하므로 바로 할당
                    )
                }
            } catch (e: Exception) {
                // 3. 실패 상태 업데이트
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "데이터 로딩에 실패했습니다: ${e.message}"
                    )
                }
            }
        }
    }
}