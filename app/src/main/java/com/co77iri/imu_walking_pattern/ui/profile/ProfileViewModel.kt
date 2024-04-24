package com.co77iri.imu_walking_pattern.ui.profile

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.MENU_SELECT
import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatient
import com.co77iri.imu_walking_pattern.network.pagingsource.ClinicalPatientPagingSource
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
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

    fun updateSearchWord(searchWord: String) = viewModelScope.launch {
        updateState(
            currentState.copy(
                searchWord = searchWord
            )
        )
    }

    fun searchProfile() = viewModelScope.launch {
        patientRepository.getClinicalPatientByEmrPatientNumber(
            currentState.searchWord
        ).onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            updateState(currentState.copy(isLoading = false))
            when (it) {
                is ApiResult.Success -> {
                    val result = it.data.result
                    if (result != null) {
                        App.selectedProfile = result
                        postEffect(ProfileContract.Effect.NavigateTo(MENU_SELECT))
                    } else {
                        postEffect(ProfileContract.Effect.ShowSnackBar("환자를 찾을 수 없습니다."))
                    }
                }

                is ApiResult.ApiError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar("API 오류가 발생했습니다."))
                }

                is ApiResult.NetworkError -> {
                    postEffect(ProfileContract.Effect.ShowSnackBar("네트워크 오류가 발생했습니다."))
                }
            }
        }
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