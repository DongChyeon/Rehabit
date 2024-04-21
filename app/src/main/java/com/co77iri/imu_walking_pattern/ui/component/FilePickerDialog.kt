package com.co77iri.imu_walking_pattern.ui.component

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.interfaces.UploadService
import com.co77iri.imu_walking_pattern.viewmodels.ProfileViewModel_old
import com.co77iri.imu_walking_pattern.viewmodels.ResultViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

enum class FilePickerDialogState {
    FilePicker, Details, Graph
}

@Composable
fun AnalysisScreen(context: Context, navController: NavController, resultViewModel: ResultViewModel) {
    var isDialogVisible by remember { mutableStateOf(true) }
    var selectedFiles by remember { mutableStateOf(listOf<File>()) }

    Row() {
        if( isDialogVisible ) {
            AlertDialog(
                onDismissRequest = { isDialogVisible = false },
                confirmButton = {
                    Button(onClick = {
                        resultViewModel.clearSelectedData()

                        // 선택된 파일들로 데이터 로딩
                        selectedFiles.forEach { file ->
                            resultViewModel.updateCSVDataFromFile(file.absolutePath)
                        }

                        resultViewModel.selectedData.value?.forEachIndexed { index, csvData ->
                            val result = csvData.myFindPeaks()
                            Log.d("test", "CSVData[$index]: $result")
                        }

                        navController.navigate("details")
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    Button( onClick = { navController.navigate("test_screen") }) {
                        Text("취소")
                    }
                },
                title = { Text(text = "파일 선택") },
                text = {
                    // 파일 목록 표시
                    val filesFolder = context.getExternalFilesDir(null)
                    val files: Array<File>? = filesFolder!!.listFiles()

                    LazyColumn {
                        items(files.orEmpty().toList()) { file ->
                            FileItem(file, selectedFiles) { selectedFile ->
                                // 파일 선택/해제 로직
                                if( selectedFiles.contains(selectedFile)) {
                                    selectedFiles = selectedFiles - selectedFile
                                } else {
                                    selectedFiles = selectedFiles + selectedFile
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun FilePickerDialog(
        context: Context,
        resultViewModel: ResultViewModel,
//        profileViewModel: ProfileViewModel,
        navController: NavController,
        isAnalysis: Boolean,
        onConfirmed: () -> Unit
) {
//    val selectedProfile = profileViewModel.selectedProfileData.value
//    val hospitalName = selectedProfile?.hospital ?: "UnknownHospital"
//    val personName = selectedProfile?.name ?: "UnknownPerson"
//    val currentTime = System.currentTimeMillis() // or other ways to get current time

    var isDialogVisible by remember { mutableStateOf(false) }
    var selectedFiles by remember { mutableStateOf(listOf<File>()) }

    // Dialog 표시 버튼
    Button( onClick = {isDialogVisible = true}) {
        if( isAnalysis ) {
            Text("측정 결과 분석")
        } else {
            Text("파일 업로드")
        }

    }

    if( isDialogVisible ) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            confirmButton = {
                Button(onClick = {
                    if( isAnalysis ) {
                        // 결과 분석일 경우
                        resultViewModel.clearSelectedData()

                        // 선택된 파일들로 데이터 로딩
                        selectedFiles.forEach { file ->
                            resultViewModel.updateCSVDataFromFile(file.absolutePath)
                        }

                        resultViewModel.selectedData.value?.forEachIndexed { index, csvData ->
                            val result = csvData.myFindPeaks()
                            Log.d("test", "CSVData[$index]: $result")
                        }

                        navController.navigate("details")
                    } else {
                        // 파일서버 업로드
                        uploadMultipleCsvFiles(context, selectedFiles)
                    }

                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button( onClick = { isDialogVisible = false }) {
                    Text("취소")
                }
            },
            title = { Text(text = "파일 선택") },
            text = {
                // 파일 목록 표시
                val notUploadedFolder = File(context.filesDir, "not_uploaded")
//                val notUploadedFolder = File(context.getExternalFilesDir(null), "not_uploaded")
                val files: Array<File>? = notUploadedFolder.listFiles()

//                val filesFolder = context.getExternalFilesDir(null)
//                val files: Array<File>? = filesFolder!!.listFiles()

                LazyColumn {
                    items(files.orEmpty().toList()) { file ->
                        FileItem(file, selectedFiles) { selectedFile ->
                            // 파일 선택/해제 로직
                            if( selectedFiles.contains(selectedFile)) {
                                selectedFiles = selectedFiles - selectedFile
                            } else {
                                selectedFiles = selectedFiles + selectedFile
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun FileItem(file: File, selectedFiles: List<File>, onFileClicked: (File) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFileClicked(file) }
            .background(if (selectedFiles.contains(file)) Color.Blue.copy(alpha = 0.2f) else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(file.name, Modifier.padding(16.dp))
        if (selectedFiles.contains(file)) {
            Icon(Icons.Default.Check, contentDescription = null)
        }
    }
}

@Composable
fun DetailsScreen(context: Context, navController: NavController, resultViewModel: ResultViewModel, profileViewModelOld: ProfileViewModel_old) {
    val firstCSVData = resultViewModel.selectedData.value?.get(0)
    val totalSteps = resultViewModel.getTotalStep()

    val totalTimeInSeconds = firstCSVData!!.getDataLength() / 60
    val cadence = (totalSteps.toDouble() / (firstCSVData.getDataLength() / 60)) * 60

    val totalWalkingDistance = resultViewModel.calculateTotalWalkingDistance()
    val avgWalkingDistance: Double = totalWalkingDistance / totalSteps
//    val avgDistanceDivHeight: Double = avgWalkingDistance / resultViewModel.userHeight.value.toDouble()
    val avgDistanceDivHeight: Double = avgWalkingDistance / (profileViewModelOld.selectedProfileData.value?.height ?: 174.0)

    val avgSpeed = totalWalkingDistance / totalTimeInSeconds
    val gaitCycleDuration = totalTimeInSeconds / totalSteps.toDouble()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("총 걸음 수 : ${totalSteps}")
        Text("총 시간 : ${firstCSVData.getDataLength() / 60}초")
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "1. Cadence : ${String.format("%.1f", cadence)}걸음/분")
        Text(text = "2. Avg Stride length : ${String.format("%.1f",avgWalkingDistance)}cm")
        Text(text = "3. Avg Stride length / Height : ${String.format("%.1f", avgDistanceDivHeight)}")
        Text(text = "4. Avg Speed : ${String.format("%.1f", avgSpeed)}cm/s")
        Text(text = "5. Gait cycleduration : ${String.format("%.1f", gaitCycleDuration)}초")

        Button(onClick = {
                         navController.navigate("test_screen")
        }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("처음으로")
        }
    }
}

@Composable
fun GraphScreen() {
    // 여기서 그래프 로직을 추가합니다.
    Text("그래프 화면")
}

fun uploadMultipleCsvFiles(context: Context, files: List<File>) {
    val multipartBodyParts: MutableList<MultipartBody.Part> = mutableListOf()
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http:/ec2-43-201-23-77.ap-northeast-2.compute.amazonaws.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    for (file in files) {
        val requestFile = file.asRequestBody("text/csv".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("csvFiles", file.name, requestFile)
        multipartBodyParts.add(body)
    }

    val uploadService = retrofit.create(UploadService::class.java)
    val call = uploadService.uploadCsv(multipartBodyParts)

    val uploadedFolder = File(context.getExternalFilesDir(null), "uploaded")
    if (!uploadedFolder.exists()) {
        uploadedFolder.mkdirs()
    }

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                println("Multiple file upload successful")
                moveFilesToUploadedDirectory(context, files)

                // UI 스레드에서 Toast 메시지 표시
                Toast.makeText(context, "파일 업로드가 완료되었습니다.", Toast.LENGTH_SHORT).show()

            } else {
                println("Multiple file upload failed: ${response.errorBody()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            println("Multiple file upload error: ${t.message}")
        }
    })
}

private fun moveFilesToUploadedDirectory(context: Context, files: List<File>) {
    val notUploadedFolder = File(context.getExternalFilesDir(null), "not_uploaded")
    val uploadedFolder = File(context.getExternalFilesDir(null), "uploaded")

    if (!uploadedFolder.exists()) {
        uploadedFolder.mkdirs()
    }

    for (file in files) {
        val sourceFile = File(notUploadedFolder, file.name)
        val destinationFile = File(uploadedFolder, file.name)

        if (sourceFile.renameTo(destinationFile)) {
            println("File moved successfully")
        } else {
            println("File move failed")
        }
    }
}

//private fun moveFilesToUploadedDirectory(files: List<File>) {
//    for( file in files ) {
//        val sourceFile = File("not_uploaded/${file.name}")
//        val destinationFile = File("uploaded/${file.name}")
//
//        if( sourceFile.renameTo(destinationFile)) {
//            println("File moved successfully")
//        } else {
//            println("File move failed")
//        }
//    }
//}