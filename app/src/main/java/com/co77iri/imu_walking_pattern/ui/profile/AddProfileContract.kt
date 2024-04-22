package com.co77iri.imu_walking_pattern.ui.profile

import androidx.navigation.NavOptions
import com.co77iri.imu_walking_pattern.UiEffect
import com.co77iri.imu_walking_pattern.UiEvent
import com.co77iri.imu_walking_pattern.UiState
import com.co77iri.imu_walking_pattern.network.models.Gender

class AddProfileContract {

    data class State(
        val isLoading: Boolean = false,
        val emrPatientNumber: String = "",
        val height: String = "",
        val birthYear: String = "",
        val gender: Gender? = null
    ) : UiState

    sealed class Event : UiEvent {
    }

    sealed class Effect : UiEffect {
        data class NavigateTo(
            val destination: String,
            val navOptions: NavOptions? = null
        ) : Effect()
        object NavigateUp: Effect()
        data class ShowSnackBar(val message: String) : Effect()
    }
}