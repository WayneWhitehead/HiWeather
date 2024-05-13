package com.hidesign.hiweather.model

sealed class UIStatus {
    data object Loading : UIStatus()
    data object Success : UIStatus()
    data class Error(val type: ErrorType) : UIStatus()
}