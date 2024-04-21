package com.co77iri.imu_walking_pattern.ui.profile

import android.content.Context
import androidx.navigation.NavOptions
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.co77iri.imu_walking_pattern.UiEffect
import com.co77iri.imu_walking_pattern.UiEvent
import com.co77iri.imu_walking_pattern.UiState
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ProfileContract {

    data class State(
        val isLoading: Boolean = false,
        val lastLoginMethod: String? = null,

        val profiles: Flow<PagingData<ClinicalPatient>> = flowOf(
            PagingData.from(
                emptyList(),
                LoadStates(
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true),
                    refresh = LoadState.NotLoading(endOfPaginationReached = true),
                )
            )
        ),
        val selectedProfile: ClinicalPatient? = null
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