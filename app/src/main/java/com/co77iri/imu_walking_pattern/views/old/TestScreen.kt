package com.co77iri.imu_walking_pattern.views.old

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ui.sensor.BluetoothViewModel
import com.co77iri.imu_walking_pattern.ui.sensor.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.co77iri.imu_walking_pattern.ui.component.FilePickerDialog
import com.co77iri.imu_walking_pattern.viewmodels.ProfileViewModel_old
import com.co77iri.imu_walking_pattern.viewmodels.ResultViewModel
enum class TestStep {
    SELECT,
    DEVICE_SCAN,
    SYNC,
    WALK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    context: Context,
    navController: NavController,
    btViewModel: BluetoothViewModel,
    sensorViewModel: SensorViewModel,
    resultViewModel: ResultViewModel,
    profileViewModelOld: ProfileViewModel_old
) {
    var currentStep by remember { mutableStateOf(TestStep.SELECT) }
    val focusRequester = FocusRequester()
    var isFocused by remember { mutableStateOf(true) }

    when( currentStep ) {
        TestStep.SELECT -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text("보행패턴 측정", fontSize = 32.sp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
//                    TextField(
////                        value = resultViewModel.userHeight.value,
//                        onValueChange = { input ->
//                            // 실시간으로 '.' 또는 숫자만 입력되는지 확인
//                            if (input.all { it.isDigit() || it == '.' }) {
//                                resultViewModel.userHeight.value = input
//                            }
//                        },
//                        label = { Text("사용자 신장") },
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            keyboardType = KeyboardType.Number
//                        ),
//                        modifier = Modifier
//                            .border(1.dp, Color.Black)
//                            .focusRequester(focusRequester)
//                            .onFocusChanged { focusState ->
//                                val lostFocus = isFocused && !focusState.isFocused
//                                if (lostFocus) {
//                                    // 포커스를 잃었을 때 유효성 검사
//                                    val parsedValue =
//                                        resultViewModel.userHeight.value.toDoubleOrNull()
//                                    if (parsedValue != null) {
//                                        resultViewModel.userHeight.value = parsedValue.toString()
//                                    } else {
//                                        // 올바르지 않은 값이 입력되었을 경우 이전 값으로 복원
//                                        resultViewModel.userHeight.value =
//                                            resultViewModel.userHeight.value
//                                    }
//                                }
//                                isFocused = focusState.isFocused
//                            },
//                        colors = TextFieldDefaults.outlinedTextFieldColors()
//                    )


                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(onClick = {
                        currentStep = TestStep.DEVICE_SCAN
                    }) {
                        Text("센서를 이용하여 데이터 수집")
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    FilePickerDialog(context, resultViewModel, navController, true) {}

                    Spacer(modifier = Modifier.height(12.dp))
                    FilePickerDialog(context, resultViewModel, navController, false) {}
//                    OutlinedButton(onClick = {
//
//                    }) {
//
//                    }
                }
            }
        }

        TestStep.DEVICE_SCAN -> {
            // 디바이스 스캔 UI
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.SpaceAround
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text("기기 연결", fontSize = 32.sp)

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

                Spacer(modifier = Modifier.height(24.dp))
                Text("스캔 결과", fontSize = 18.sp)

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

                Spacer(modifier = Modifier.height(24.dp))
                Text("연결된 기기 목록", fontSize = 18.sp)
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

                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(onClick = {
                    val connSensors: ArrayList<XsensDotDevice> = ArrayList(sensorViewModel.sensorList)
                    currentStep = TestStep.SYNC

                    if(connSensors.isNotEmpty()) {
                        sensorViewModel.startSync(connSensors)
                    }
                }) {
                    Text("Sync 시작")
                }
            }
        }

        TestStep.SYNC -> {
//            if( sensorViewModel.syncStatus.value >= 2 ) {
//                currentStep = TestStep.WALK
//            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if( sensorViewModel.syncStatus.value < 2) {
                    Text("Sync 진행중\n${sensorViewModel.syncProgress.value}%", fontSize = 24.sp, textAlign = TextAlign.Center)
                } else {
                    Text("충분한 공간을 확보한 상태에서 측정 시작 버튼을 눌러주세요.", fontSize = 18.sp)
                    OutlinedButton(onClick = {
                        val connSensors: ArrayList<XsensDotDevice> = ArrayList(sensorViewModel.sensorList)

                        sensorViewModel.isMeasuring.value = true
                        sensorViewModel.setAllDeviceMeasurement(connSensors)

                        sensorViewModel.createFiles(connSensors)
                        Toast.makeText(context,"측정이 시작됩니다.", Toast.LENGTH_SHORT ).show()

                        sensorViewModel.setMeasurement( sensorViewModel.isMeasuring.value )

                        currentStep = TestStep.WALK
                    }) {
                        Text("측정 시작")
                    }
                }

            }
        }

        TestStep.WALK -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text("측정 종료시에\n아래 버튼을 눌러주세요.", fontSize = 18.sp, textAlign = TextAlign.Center)
                OutlinedButton(onClick ={
                    sensorViewModel.isMeasuring.value = false

                    sensorViewModel.closeFiles()
                    sensorViewModel.setMeasurement( sensorViewModel.isMeasuring.value )

                    currentStep = TestStep.SELECT
                }) {
                    Text("측정 종료")
                }

            }
        }
    }
}

