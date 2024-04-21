package com.co77iri.imu_walking_pattern.ui.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.models.ProfileData
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatient
import com.co77iri.imu_walking_pattern.network.pagingsource.ClinicalPatientPagingSource
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : BaseViewModel<ProfileContract.State, ProfileContract.Event, ProfileContract.Effect>(
    initialState = ProfileContract.State()
) {
    override fun reduceState(event: ProfileContract.Event) {

    }

    fun getProfiles() = viewModelScope.launch {
        updateState(
            currentState.copy(
                profiles = Pager(
                    config = PagingConfig(
                        pageSize = 10
                    ),
                    pagingSourceFactory = {
                        ClinicalPatientPagingSource(patientRepository)
                    }
                ).flow.cachedIn(viewModelScope)
            )
        )
    }

    fun updateSelectedProfile(profile: ClinicalPatient) {
        updateState(
            currentState.copy(
                selectedProfile = profile
            )
        )
    }
}