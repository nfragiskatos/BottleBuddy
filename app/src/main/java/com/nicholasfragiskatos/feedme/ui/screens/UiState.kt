package com.nicholasfragiskatos.feedme.ui.screens

data class UiState<out T>(
    val data: T,
    val isLoading: Boolean = false
)