package com.co77iri.imu_walking_pattern.ui.upload

import androidx.navigation.NavOptions
import com.co77iri.imu_walking_pattern.UiEffect
import com.co77iri.imu_walking_pattern.UiEvent
import com.co77iri.imu_walking_pattern.UiState

class UploadResultContract {

    companion object {
        const val PLACEHOLDER_TEXT = "단계를 설정해주세요"
    }

    data class State(
        val isLoading: Boolean = false,
        val testDateAndTime: String = "",
        val totalTimeInSeconds: Double = 0.0,
        val parkinsonStage: String = PLACEHOLDER_TEXT
    ) : UiState

    sealed class Event : UiEvent

    sealed class Effect : UiEffect {
        data class NavigateTo(
            val destination: String,
            val navOptions: NavOptions? = null
        ) : Effect()
        object NavigateUp: Effect()
        data class ShowSnackBar(val message: String) : Effect()
    }
}