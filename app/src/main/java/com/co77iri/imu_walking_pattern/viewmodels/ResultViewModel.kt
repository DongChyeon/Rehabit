package com.co77iri.imu_walking_pattern.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.co77iri.imu_walking_pattern.models.CSVData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File

class ResultViewModel : ViewModel() {
    data class CsvSession(
        val leftFile: File,
        val rightFile: File
    )

    private val _csvFiles = mutableStateOf<List<CsvSession>>(listOf())
    val csvFiles: State<List<CsvSession>> = _csvFiles

    fun loadcsvFiles(directory: File) {
        Log.d("Files", "Path: ${directory.absolutePath}")
        val allFiles = directory.listFiles { _, name ->
            name.endsWith("_L.csv") || name.endsWith("_R.csv")
        } ?: arrayOf()

        val groupedFiles = allFiles.groupBy { file ->
            file.nameWithoutExtension.substringBeforeLast("_")
        }

        _csvFiles.value = groupedFiles.values.mapNotNull { fileList ->
            val leftFile = fileList.find { it.name.endsWith("_L.csv") }
            val rightFile = fileList.find { it.name.endsWith("_R.csv") }
            if (leftFile != null && rightFile != null) {
                CsvSession(leftFile, rightFile)
            } else {
                null
            }

        }

        _csvFiles.value.forEach {
            Log.d("Files", "File: ${it.leftFile.name}")
            Log.d("Files", "File: ${it.rightFile.name}")
        }
    }

    val selectedData = MutableLiveData<List<CSVData>>(emptyList())
    val allSquaresForBothFeet = mutableListOf<List<Double>>() // 왼발 오른발 저장

    var uploadTitle = mutableStateOf("")
    fun updateCSVDataFromFile( filename: String ) {
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

            selectedData.value = selectedData.value?.plus(updateData) ?: listOf(updateData)
        } else {
            Log.d(TAG,"File is not exist")
        }
    }

    fun clearSelectedData() {
        selectedData.value = emptyList()
    }

    fun getStep(csvData: CSVData): Int {
        val peaks: Pair<List<Double>, List<Int>> = csvData.myFindPeaks()
        val steps = peaks.second.size

        return steps
    }

    fun getTotalStep(): Int {
        var allPeaks = mutableStateOf(listOf<Pair<List<Double>, List<Int>>>())
        selectedData.value?.forEach { csvData ->
            val peaks = csvData.myFindPeaks()
            allPeaks.value = allPeaks.value + peaks
        }

        val totalSteps = allPeaks.value.sumOf { it.second.size }

        return totalSteps
    }

    fun getFirstStepSquaredSum(): Double {
        val csvDataInstance = selectedData.value?.get(0) ?: return 0.0
        val peaks = csvDataInstance.myFindPeaks()

        if (peaks.second.size < 2) return 0.0 // Not enough peaks

        return csvDataInstance.squaredSumBetweenPeaks(peaks.second[0], peaks.second[1])
    }
    private fun updateAllStepsSquaredSums() {
        allSquaresForBothFeet.clear()

        selectedData.value?.forEach { csvData ->
            val data = getStepsSquaredSumsForCSVData(csvData)
            allSquaresForBothFeet.add(data)
        }
    }

    private fun getStepsSquaredSumsForCSVData(csvData: CSVData): List<Double> {
        val squaredSums = mutableListOf<Double>()
        val peaks = csvData.myFindPeaks()

        for (i in 0 until peaks.second.size - 1) {
            squaredSums.add(csvData.squaredSumBetweenPeaks(peaks.second[i], peaks.second[i + 1]))
        }

        return squaredSums
    }

//    fun calculateStrideLengthFromSquaredSum(squaredSum: Double): Double {
//        val calibrationValue = 65.0 // 주어진 A 값에 해당하는 거리 (cm)
//        val calibrationSquaredSum = ... // 이 부분에는 실제 실험을 통해 얻은 A 값을 넣어야 합니다.
//
//        return (squaredSum / calibrationSquaredSum) * calibrationValue
//    }

    // 첫번쨰 걸음값으로 캘리값 대체
    private fun calculateStrideLengthFromSquaredSum(squaredSum: Double, calibrationSquaredSum: Double): Double {
        val calibrationValue = 65.0 // 주어진 A 값에 해당하는 거리 (cm)

        return (squaredSum / calibrationSquaredSum) * calibrationValue
    }

    fun calculateTotalWalkingDistance(): Double {
        updateAllStepsSquaredSums() // 먼저 모든 제곱 합을 업데이트합니다.

        val calibrationSquaredSum = allSquaresForBothFeet[0][0] // 왼발의 첫 걸음 제곱 합으로 대체

        var totalDistance = 0.0

        allSquaresForBothFeet.forEach { squaredSumsForOneFoot ->
            squaredSumsForOneFoot.forEach { squaredSum ->
                totalDistance += calculateStrideLengthFromSquaredSum(squaredSum, calibrationSquaredSum)
            }
        }

        // 총 걸음 수에서 시작과 끝의 걸음을 제외하기 위해 2 걸음의 거리를 뺍니다.
        val avgStrideLength = totalDistance / (getTotalStep() - 2)

        return avgStrideLength * (getTotalStep() - 2)
    }

    companion object {
        const val TAG = "ResultViewModel"
    }
}