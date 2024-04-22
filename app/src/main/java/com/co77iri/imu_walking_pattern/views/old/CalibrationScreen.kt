package com.co77iri.imu_walking_pattern.views.old

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ui.sensor.BluetoothViewModel
import com.co77iri.imu_walking_pattern.viewmodels.ResultViewModel
import com.co77iri.imu_walking_pattern.ui.sensor.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice

enum class CalibrationStep {
    DEVICE_SCAN,
    SYNC,
    CALIBRATION_START,
    WALK,
    ADJUST_PARAMS
}

@Composable
fun CalibrationScreen (
    context: Context,
    navController: NavController,
    btViewModel: BluetoothViewModel,
    sensorViewModel: SensorViewModel,
    resultViewModel: ResultViewModel
) {
    var currentStep by remember { mutableStateOf(CalibrationStep.DEVICE_SCAN) }

    when (currentStep) {
        CalibrationStep.DEVICE_SCAN -> {
            // 디바이스 스캔 UI
            Column {
                Text("1. 기기 연결", fontSize = 20.sp)

                OutlinedButton(onClick = {
                    if( !btViewModel.isScanning.value ) {
                        btViewModel.startScan()
                    } else {
                        btViewModel.stopScan()
                    }
                }) {
                    if(!btViewModel.isScanning.value){
                        Text("스캔 시작")
                    } else {
                        Text("스캔 중지")
                    }
                }

                Text("스캔 결과")

                for( deviceMap in btViewModel.scannedSensorList) {
                    val device = deviceMap["device"] as? BluetoothDevice
                    val state = deviceMap["state"] as? Int

                    var isExist = false

                    if( device != null && state != null ) {
                        Row {
                            Text("Address: ${device.address}")
                            TextButton(onClick = {
                                if( btViewModel.isScanning.value ) {
                                    btViewModel.stopScan()
                                }

                                if(sensorViewModel.sensorList.isNotEmpty()) {
                                    val it: Iterator<XsensDotDevice> = sensorViewModel.sensorList.iterator()
                                    while( it.hasNext() ) {
                                        val sensorListDevice = it.next()

                                        if( sensorListDevice.address == device.address ) {
                                            isExist = true
                                            break
                                        }
                                    }
                                }

                                if( isExist ) {
                                    sensorViewModel.disconnectSensor(device.address)
                                    sensorViewModel.removeDevice(device.address)

                                    Log.d("ScanScreen", "disconnect and remove sensor ${device.address}")
                                } else {
                                    sensorViewModel.connectSensor( device )
                                    Log.d("ScanScreen", "connect Sensor ${device.address}")

                                }
                            }) {
                                Text("연결")
                            }
                        }

                    }
                }

                Text("연결된 기기 목록")
                Column {
                    if(sensorViewModel.sensorList.isNotEmpty()) {
                        val it: Iterator<XsensDotDevice> = sensorViewModel.sensorList.iterator()
                        while( it.hasNext() ) {
                            val device = it.next()

//                                        Text("Address: ${device.address}, 상태: ${device.connectionState}")
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Text("Address: ${device.address}")
                                TextButton(onClick = {
                                    sensorViewModel.disconnectSensor(device.address)
                                }) {
                                    Text("연결 해제")
                                }
                            }
                        }
                    }
                }

                OutlinedButton(onClick = {
                    val connSensors: ArrayList<XsensDotDevice> = ArrayList(sensorViewModel.sensorList)
                    currentStep = CalibrationStep.SYNC

                    if(connSensors.isNotEmpty()) {
                        sensorViewModel.startSync(connSensors)
                    }
                }) {
                    Text("Sync 시작")
                }
            }
        }
        CalibrationStep.SYNC -> {
            // Sync UI
            if( sensorViewModel.syncStatus.value >= 2 ) {
                currentStep = CalibrationStep.CALIBRATION_START
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sync 진행중\n${sensorViewModel.syncProgress.value}%", fontSize = 24.sp)
            }
        }
        CalibrationStep.CALIBRATION_START -> {
            val connSensors: ArrayList<XsensDotDevice> = ArrayList(sensorViewModel.sensorList)
//            var caliResultsFileName: Array<String> = emptyArray<String>()
            var caliResultsFileName by remember { mutableStateOf(emptyArray<String>()) }

            // 캘리브레이션 시작 UI
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var calibrationStartIdx by remember { mutableStateOf(0) }
                Text("캘리브레이션 준비", fontSize = 32.sp)

                when ( calibrationStartIdx ) {
                    0 -> {
                        Text("일어서서 정면을 바라보세요.\n 총 10걸음을 걸을 수 있도록 공간을 확보하세요.")
                        OutlinedButton(onClick = {
                            calibrationStartIdx += 1
                        }) {
                            Text("다음")
                        }
                    }

                    1 -> {
                        Text("이제 아래 버튼을 누르고 1초 뒤부터 10걸음을 걸으세요.\n왼발 5걸음, 오른발 5걸음 총 10걸음입니다.")
                        OutlinedButton(onClick = {
                            sensorViewModel.isMeasuring.value = true
                            sensorViewModel.setAllDeviceMeasurement(connSensors)
                            caliResultsFileName = sensorViewModel.createCalibrationFiles(connSensors)

                            sensorViewModel.setMeasurement( sensorViewModel.isMeasuring.value )

                            calibrationStartIdx += 1
                        }) {
                            Text("측정 시작")
                        }
                    }

                    2 -> {
                        Text("측정 중입니다.")

                        OutlinedButton(onClick = {
                            sensorViewModel.isMeasuring.value = false
//                            sensorViewModel.setAllDeviceMeasurement(connSensors)
                            sensorViewModel.closeFiles()

                            calibrationStartIdx += 1
                        }) {
                            Text("측정 종료")
                        }
                    }

                    3 -> {
                        resultViewModel.clearSelectedData()

                        caliResultsFileName.forEach { fileName ->
                            resultViewModel.updateCSVDataFromFile(fileName)
                        }

                        resultViewModel.selectedData.value?.forEachIndexed { index, csvData ->
                            val result = csvData.myFindPeaks()
                            Log.d("test", "CSVData[$index]: $result")
                        }

                        val toastText = resultViewModel.selectedData.value?.joinToString(separator = "\n") { csvData ->
                            val result = csvData.myFindPeaks()
                            val index = resultViewModel.selectedData.value?.indexOf(csvData) ?: -1
                            val lengthOfFirstList = result.first.size // 첫 번째 리스트의 길이를 가져옵니다.
                            "${index}번째 센서: $lengthOfFirstList 걸음"
                        } ?: "No Data Available"

                        Text("걸음 결과")
//                        Text("총 N걸음 걸으셨습니다.")
                        Text(toastText)
                    }

                }
            }

        }
        CalibrationStep.WALK -> {
            // 걷기 UI
        }
        CalibrationStep.ADJUST_PARAMS -> {
            // 파라미터 조절 UI
        }
    }
}
