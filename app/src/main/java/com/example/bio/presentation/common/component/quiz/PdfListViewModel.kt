package com.example.bio.presentation.common.component.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bio.presentation.common.component.chat.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PdfListUiState {
    object Loading : PdfListUiState()
    data class Success(val pdfs: List<String>) : PdfListUiState()
    data class Error(val message: String) : PdfListUiState()
}

@HiltViewModel
class PdfListViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow<PdfListUiState>(PdfListUiState.Loading)
    val uiState: StateFlow<PdfListUiState> = _uiState

    init {
        loadPdfList()
    }

    fun loadPdfList() {
        viewModelScope.launch {
            _uiState.value = PdfListUiState.Loading
            try {
                val response = apiService.getPdfList()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = PdfListUiState.Success(response.body()!!.filenames)
                } else {
                    _uiState.value = PdfListUiState.Error("Failed to fetch PDF list: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = PdfListUiState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
}