package com.falcon.otpbymodiji

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.falcon.otpbymodiji.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val repository: ApiRepository
) : ViewModel() {

    private val _showWalkthrough = MutableStateFlow(true)
    val showWalkthrough: StateFlow<Boolean> get() = _showWalkthrough

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> get() = _uiState

    init {
        viewModelScope.launch {
            _showWalkthrough.value = preferencesManager.isFirstTimeUser.first()
        }
    }

    fun setShowWalkthrough(show: Boolean) {
        viewModelScope.launch {
            preferencesManager.setFirstTimeUser(!show)
            _showWalkthrough.value = show
        }
    }

    fun registerAndSendOtp(mobile: String, count: Int) {
        viewModelScope.launch {
            val response = repository.register(mobile)
            if (response.status == 200) {
                for (i in 1..count) {
                    val otpResponse = repository.sendOtp(mobile)
                    if (otpResponse.status == "success") {
                        _uiState.value =  UiState("OTP Sent: $i/$count")
                    }
                    else if (otpResponse.error?.isEmpty() == false){
                        _uiState.value = UiState("Mobile Number WhiteListed")
                        return@launch
                    }
                    else {
                        _uiState.value = UiState(
                            statusMessage = "Error: Failed to send OTP at $i/$count"
                        )
                        break
                    }
                }
            } else {
                _uiState.value = UiState(
                    statusMessage = "Error: ${response.message}"
                )
            }
        }
    }
}
