package com.co77iri.imu_walking_pattern.ui.csv

import androidx.navigation.NavOptions
import com.co77iri.imu_walking_pattern.UiEffect
import com.co77iri.imu_walking_pattern.UiEvent
import com.co77iri.imu_walking_pattern.UiState
import com.co77iri.imu_walking_pattern.models.CSVData

class CsvResultContract {

    data class State(
        val isLoading: Boolean = false,
        val testDateAndTime: String = "",
        val totalTimeInSeconds: String = "",
        val parkinsonStage: Int = 1,
        val averageSpeed: Double = 0.0,
        val cadence: Int = 0,
        val gaitCycle: Double = 0.0,
        val totalSteps: Int = 0,
        val leftSteps: Int = 0,
        val rightSteps: Int = 0,
        val strideLength: Double = 0.0,
        val leftStrideLength: Double = 0.0,
        val rightStrideLength: Double = 0.0,
        val stepLength: Double = 0.0,
        val leftStepLength: Double = 0.0,
        val rightStepLength: Double = 0.0,
        val csvDataLeft: CSVData = CSVData(),
        val csvDataRight: CSVData = CSVData()
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