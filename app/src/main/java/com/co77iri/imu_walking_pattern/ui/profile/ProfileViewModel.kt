package com.co77iri.imu_walking_pattern.ui.profile

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.network.pagingsource.ClinicalPatientPagingSource
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
}