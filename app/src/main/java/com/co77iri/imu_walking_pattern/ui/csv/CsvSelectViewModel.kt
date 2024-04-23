package com.co77iri.imu_walking_pattern.ui.csv

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.network.pagingsource.ParkinsonTestDataPagingSource
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CsvSelectViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : BaseViewModel<CsvSelectContract.State, CsvSelectContract.Event, CsvSelectContract.Effect>(
    initialState = CsvSelectContract.State()
) {
    override fun reduceState(event: CsvSelectContract.Event) {

    }

    fun getParkinsonTestDataList(patientId: Int) = viewModelScope.launch {
        updateState(
            currentState.copy(
                results = Pager(
                    config = PagingConfig(
                        pageSize = 10
                    ),
                    pagingSourceFactory = {
                        ParkinsonTestDataPagingSource(patientRepository, patientId)
                    }
                ).flow.cachedIn(viewModelScope)
            )
        )
    }
}