package com.co77iri.imu_walking_pattern.ui.upload

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.models.CSVData
import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadResultViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): BaseViewModel<UploadResultContract.State, UploadResultContract.Event, UploadResultContract.Effect>(
    initialState = UploadResultContract.State()
) {
    override fun reduceState(event: UploadResultContract.Event) { }

    fun updateTestDateAndTime(testDateAndTime: String) {
        updateState(
            currentState.copy(
                testDateAndTime = testDateAndTime
            )
        )
    }

    fun updateTotalTimeInSeconds(totalTimeInSeconds: Double) {
        updateState(
            currentState.copy(
                totalTimeInSeconds = totalTimeInSeconds
            )
        )
    }

    fun updateParkinsonStage(parkinsonStage: String) {
        updateState(
            currentState.copy(
                parkinsonStage = parkinsonStage
            )
        )
    }

    fun uploadCsvFiles(
        leftFile: File, rightFile: File
    ) = viewModelScope.launch {
        patientRepository.postParksonTestData(
            App.selectedProfile?.clinicalPatientId!!,
            currentState.testDateAndTime,
            currentState.totalTimeInSeconds,
            currentState.parkinsonStage.toInt(),
            leftFile, rightFile
        ).collect {
            when (it) {
                is ApiResult.Success -> {
                    postEffect(UploadResultContract.Effect.ShowSnackBar("업로드 성공!"))
                    delay(500)
                    postEffect(UploadResultContract.Effect.NavigateUp)
                }

                is ApiResult.ApiError -> {
                    postEffect(UploadResultContract.Effect.ShowSnackBar(it.message))
                }

                is ApiResult.NetworkError -> {
                    postEffect(UploadResultContract.Effect.ShowSnackBar("네트워크 오류가 발생했습니다."))
                }
            }
        }
    }

    val allSquaresForBothFeet = mutableListOf<List<Double>>() // 왼발 오른발 저장

    fun updateCSVDataFromFile(filename: String): CSVData {
        val updateData = CSVData()
        val file = File(filename)

        if (file.exists()) {
            var lineNumber = 0

            file.forEachLine { line ->
                lineNumber++

                // 1~11번 라인은 설명이므로 무시
                if (lineNumber <= 11) return@forEachLine

                // 12번 라인은 헤더이므로 무시
                if (lineNumber == 12) return@forEachLine

                // 13번 라인부터 데이터를 파싱
                val parts = line.split(",")
                if (parts.size >= 9) {
                    val freeAccX = parts[6].trim().toDoubleOrNull()
                    val freeAccY = parts[7].trim().toDoubleOrNull()
                    val freeAccZ = parts[8].trim().toDoubleOrNull()

                    if (freeAccX != null && freeAccY != null && freeAccZ != null) {
                        updateData.FreeAccX.add(freeAccX)
                        updateData.FreeAccY.add(freeAccY)
                        updateData.FreeAccZ.add(freeAccZ)
                    }
                }
            }
        } else {
            Log.d(TAG, "File is not exist")
        }

        return updateData
    }

    fun getTotalStep(
        leftCSVData: CSVData,
        rightCSVData: CSVData
    ): Int {
        var allPeaks = mutableListOf<Pair<List<Double>, List<Int>>>()
        val leftPeaks = leftCSVData.myFindPeaks()
        val rightPeaks = rightCSVData.myFindPeaks()
        allPeaks.addAll(listOf(leftPeaks))
        allPeaks.addAll(listOf(rightPeaks))

        val totalSteps = allPeaks.sumOf { it.second.size }

        return totalSteps
    }

    private fun updateAllStepsSquaredSums(
        leftCSVData: CSVData,
        rightCSVData: CSVData
    ) {
        allSquaresForBothFeet.clear()

        val leftData = getStepsSquaredSumsForCSVData(leftCSVData)
        val rightData = getStepsSquaredSumsForCSVData(rightCSVData)
        allSquaresForBothFeet.add(leftData)
        allSquaresForBothFeet.add(rightData)
    }

    private fun getStepsSquaredSumsForCSVData(csvData: CSVData): List<Double> {
        val squaredSums = mutableListOf<Double>()
        val peaks = csvData.myFindPeaks()

        for (i in 0 until peaks.second.size - 1) {
            squaredSums.add(csvData.squaredSumBetweenPeaks(peaks.second[i], peaks.second[i + 1]))
        }

        return squaredSums
    }

    // 첫번쨰 걸음값으로 캘리값 대체
    private fun calculateStrideLengthFromSquaredSum(squaredSum: Double, calibrationSquaredSum: Double): Double {
        val calibrationValue = 65.0 // 주어진 A 값에 해당하는 거리 (cm)

        return (squaredSum / calibrationSquaredSum) * calibrationValue
    }

    companion object {
        const val TAG = "ResultViewModel"
    }
}