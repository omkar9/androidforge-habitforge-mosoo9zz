package com.androidforge.habitforge.core.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable? = null, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    object Empty : Result<Nothing>() // For empty data sets, e.g., no habits found
    object Offline : Result<Nothing>() // Specific for network connectivity issues
}