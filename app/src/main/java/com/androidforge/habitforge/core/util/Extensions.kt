package com.androidforge.habitforge.core.util

fun <T> kotlin.Result<T>.toAppResult(): Result<T> {
    return fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(it, it.localizedMessage) }
    )
}