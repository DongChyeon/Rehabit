package com.co77iri.imu_walking_pattern.ui.profile

import androidx.lifecycle.viewModelScope
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.models.Gender
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProfileViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : BaseViewModel<AddProfileContract.State, AddProfileContract.Event, AddProfileContract.Effect>(
    initialState = AddProfileContract.State()
) {
    override fun reduceState(event: AddProfileContract.Event) { }

    fun addProfile() = viewModelScope.launch{
        val emrPatientNumber = currentState.emrPatientNumber
        val height = currentState.height
        val birthYear = currentState.birthYear
        val gender = currentState.gender

        if (
            emrPatientNumber.isEmpty() ||
            height.isEmpty() ||
            birthYear.isEmpty() ||
            gender == null
        ) {
            return@launch
        }

        patientRepository.postClinicalPatient(
            emrPatientNumber = emrPatientNumber,
            height = height,
            birthYear = birthYear,
            gender = gender
        ).onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            when (it) {
                is ApiResult.Success -> {
                    updateState(currentState.copy(isLoading = false))
                    postEffect(AddProfileContract.Effect.NavigateUp)
                }

                is ApiResult.ApiError -> {
                    postEffect(AddProfileContract.Effect.ShowSnackBar(it.message))
                }

                is ApiResult.NetworkError -> {
                    postEffect(AddProfileContract.Effect.ShowSnackBar("네트워크 오류가 발생했습니다."))
                }
            }
        }
    }

    fun updateEmrPatientNumber(emrPatientNumber: String) {
        updateState(
            currentState.copy(
                emrPatientNumber = emrPatientNumber
            )
        )
    }

    fun updateHeight(height: String) {
        updateState(
            currentState.copy(
                height = height
            )
        )
    }

    fun updateBirthYear(birthYear: String) {
        updateState(
            currentState.copy(
                birthYear = birthYear
            )
        )
    }

    fun updateGender(gender: Gender) {
        updateState(
            currentState.copy(
                gender = gender
            )
        )
    }
}