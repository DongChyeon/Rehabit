package com.co77iri.imu_walking_pattern.ui.sensor

import androidx.navigation.NavOptions
import com.co77iri.imu_walking_pattern.UiEffect
import com.co77iri.imu_walking_pattern.UiEvent
import com.co77iri.imu_walking_pattern.UiState
import com.co77iri.imu_walking_pattern.ui.profile.AddProfileContract

class SensorContract {

    data class State(
        val isLoading: Boolean = false
    ) : UiState

    sealed class Event : UiEvent {
    }

    sealed class Effect : UiEffect {
        data class NavigateTo(
            val destination: String,
            val navOptions: NavOptions? = null
        ) : Effect()

        data class ShowSnackBar(val message: String) : Effect()
    }

}