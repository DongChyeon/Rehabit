package com.co77iri.imu_walking_pattern.ui.upload

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import com.co77iri.imu_walking_pattern.App
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.MENU_SELECT
import com.co77iri.imu_walking_pattern.PROFILE
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
        averageSpeed: Double,
        cadence: Int,
        gaitCycle: Double,
        totalSteps: Int,
        leftSteps: Int,
        rightSteps: Int,
        strideLength: Double,
        leftStrideLength: Double,
        rightStrideLength: Double,
        stepLength: Double,
        leftStepLength: Double,
        rightStepLength: Double,
        leftFile: File,
        rightFile: File
    ) = viewModelScope.launch {
        patientRepository.postParksonTestData(
            patientId = App.selectedProfile?.clinicalPatientId!!,
            testDateAndTime = currentState.testDateAndTime,
            testDuration = currentState.totalTimeInSeconds,
            parkinsonStage = try {
                currentState.parkinsonStage.toInt()
            } catch (e: Exception) {
                0
            },
            averageSpeed = averageSpeed,
            cadence = cadence,
            gaitCycle = gaitCycle,
            totalSteps = totalSteps,
            leftSteps = leftSteps,
            rightSteps = rightSteps,
            strideLength = strideLength,
            leftStrideLength = leftStrideLength,
            rightStrideLength = rightStrideLength,
            stepLength = stepLength,
            leftStepLength = leftStepLength,
            rightStepLength = rightStepLength,
            csvFileLeft = leftFile,
            csvFileRight = rightFile
        ).collect {
            when (it) {
                is ApiResult.Success -> {
                    postEffect(UploadResultContract.Effect.ShowSnackBar("업로드 성공!"))
                    delay(200)
                    postEffect(UploadResultContract.Effect.NavigateTo(
                        destination = PROFILE,
                        navOptions = navOptions {
                            popUpTo(PROFILE) {
                                inclusive = true
                            }
                        }
                    ))
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

    fun getStep(csvData: CSVData): Int {
        val peaks: Pair<List<Double>, List<Int>> = csvData.myFindPeaks()
        val steps = peaks.second.size

        return steps
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

    fun calculateTotalWalkingDistance(
        leftCSVData: CSVData,
        rightCSVData: CSVData
    ): Double {
        updateAllStepsSquaredSums(
            leftCSVData, rightCSVData
        ) // 먼저 모든 제곱 합을 업데이트합니다.

        val calibrationSquaredSum = try {
            allSquaresForBothFeet[0][0]
        } catch (e: Exception) {
            0.0
        }   // 왼발의 첫 걸음 제곱 합으로 대체

        var totalDistance = 0.0

        allSquaresForBothFeet.forEach { squaredSumsForOneFoot ->
            squaredSumsForOneFoot.forEach { squaredSum ->
                totalDistance += calculateStrideLengthFromSquaredSum(squaredSum, calibrationSquaredSum)
            }
        }

        // 총 걸음 수에서 시작과 끝의 걸음을 제외하기 위해 2 걸음의 거리를 뺍니다.
        val avgStrideLength = totalDistance / (getTotalStep(leftCSVData, rightCSVData) - 2)

        return avgStrideLength * (getTotalStep(leftCSVData, rightCSVData) - 2)
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