package com.co77iri.imu_walking_pattern.ui.csv

import androidx.navigation.NavOptions
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.airbnb.lottie.L
import com.co77iri.imu_walking_pattern.UiEffect
import com.co77iri.imu_walking_pattern.UiEvent
import com.co77iri.imu_walking_pattern.UiState
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatient
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestData
import com.co77iri.imu_walking_pattern.ui.profile.AddProfileContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CsvSelectContract {

    data class State(
        val isLoading: Boolean = false,
        val results: Flow<PagingData<ParkinsonTestData>> = flowOf(
            PagingData.from(
                emptyList(),
                LoadStates(
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true),
                    refresh = LoadState.NotLoading(endOfPaginationReached = true),
                )
            )
        )
    ) : UiState

    sealed class Event : UiEvent

    sealed class Effect : UiEffect {
        data class NavigateTo(
            val destination: String,
            val navOptions: NavOptions? = null
        ) : Effect()
        object NavigateUp : Effect()
        data class ShowSnackBar(val message: String) : Effect()
    }
}