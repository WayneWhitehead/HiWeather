package com.hidesign.hiweather.data.model

sealed class UIStatus {
    data object Success : UIStatus()
    data class Error(val type: ErrorType) : UIStatus()
}