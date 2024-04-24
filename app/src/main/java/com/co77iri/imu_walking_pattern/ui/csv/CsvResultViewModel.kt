package com.co77iri.imu_walking_pattern.ui.csv

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.co77iri.imu_walking_pattern.BaseViewModel
import com.co77iri.imu_walking_pattern.models.CSVData
import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class CsvResultViewModel @Inject constructor(
    private val patientRepository: PatientRepository
): BaseViewModel<CsvResultContract.State, CsvResultContract.Event, CsvResultContract.Effect>(
    initialState = CsvResultContract.State()
) {
    override fun reduceState(event: CsvResultContract.Event) { }

    fun getTestData(id: Int) = viewModelScope.launch {
        patientRepository.getParkinsonTestDataById(id).onStart {
            updateState(currentState.copy(isLoading = true))
        }.collect {
            when (it) {
                is ApiResult.Success -> {
                    val result = it.data.result

                    updateState(
                        currentState.copy(
                            testDateAndTime = result.testDateAndTime,
                            parkinsonStage = result.parkinsonStage,
                            csvDataLeft = updateCSVDataFromFile(result.csvFileUrlLeft),
                            csvDataRight = updateCSVDataFromFile(result.csvFileUrlRight)
                        )
                    )
                }

                is ApiResult.ApiError -> {
                    postEffect(CsvResultContract.Effect.ShowSnackBar("API 오류가 발생했습니다."))
                }

                is ApiResult.NetworkError -> {
                    postEffect(CsvResultContract.Effect.ShowSnackBar("네트워크 오류가 발생했습니다."))
                }
            }
        }
    }

    val allSquaresForBothFeet = mutableListOf<List<Double>>() // 왼발 오른발 저장

    suspend fun updateCSVDataFromFile(fileUrl: String): CSVData {
        return withContext(Dispatchers.IO) {
            val updateData = CSVData()

            try {
                val url = URL(fileUrl)
                val connection = url.openConnection()
                val inputStream = connection.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputStream))

                repeat(11) { reader.readLine() }

                reader.readLine()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val parts = line?.split(",") ?: continue
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

                reader.close()
                inputStream.close()

                updateData
            } catch (e: Exception) {
                e.printStackTrace()
                CSVData()
            }
        }
    }


    fun getTotalStep(): Int {
        val leftCSVData = currentState.csvDataLeft
        val rightCSVData = currentState.csvDataRight

        var allPeaks = mutableListOf<Pair<List<Double>, List<Int>>>()
        val leftPeaks = leftCSVData.myFindPeaks()
        val rightPeaks = rightCSVData.myFindPeaks()
        allPeaks.addAll(listOf(leftPeaks))
        allPeaks.addAll(listOf(rightPeaks))

        val totalSteps = allPeaks.sumOf { it.second.size }

        return totalSteps
    }

    private fun updateAllStepsSquaredSums() {
        val leftCSVData = currentState.csvDataLeft
        val rightCSVData = currentState.csvDataRight

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