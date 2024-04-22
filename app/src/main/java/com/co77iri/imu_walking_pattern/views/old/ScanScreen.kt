package com.co77iri.imu_walking_pattern.views.old

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ui.sensor.BluetoothViewModel
import com.co77iri.imu_walking_pattern.ui.sensor.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice
import androidx.compose.material3.TextField
import com.co77iri.imu_walking_pattern.viewmodels.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    context: Context,
    navController: NavController,
    btViewModel: BluetoothViewModel,
    sensorViewModel: SensorViewModel,
    resultViewModel: ResultViewModel
        ) {
        var minPeakHeight by remember { mutableStateOf(10.0) }
        var minPeakDistance by remember { mutableStateOf(40) }

        Column {
                Spacer(modifier = Modifier.height(30.dp))
                OutlinedButton(onClick = {
                        if(!btViewModel.isScanning.value){
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
                Spacer(modifier = Modifier.height(20.dp))
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


                Spacer(modifier = Modifier.height(20.dp))
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

                Button(onClick = {
                        val connSensors: ArrayList<XsensDotDevice> = ArrayList(sensorViewModel.sensorList)

                        when( sensorViewModel.syncStatus.value ) {
                                0 -> {
                                        if(connSensors.isNotEmpty()) {
                                                Toast.makeText(context,"Sync start", Toast.LENGTH_SHORT ).show()
                                                sensorViewModel.startSync(connSensors)
                                                Toast.makeText(context,"Sync done", Toast.LENGTH_SHORT ).show()
                                        }
                                }

                                2 -> {  // Start & Stop Measuring
                                        sensorViewModel.isMeasuring.value = !sensorViewModel.isMeasuring.value
                                        sensorViewModel.setAllDeviceMeasurement(connSensors)

                                        if(sensorViewModel.isMeasuring.value) {
                                                sensorViewModel.createFiles(connSensors)
                                                Toast.makeText(context,"Start", Toast.LENGTH_SHORT ).show()
                                        } else {
                                                Toast.makeText(context,"Stop", Toast.LENGTH_SHORT ).show()
                                                sensorViewModel.closeFiles()
                                        }

                                        sensorViewModel.setMeasurement( sensorViewModel.isMeasuring.value )
                                }
                        }

                }) {
                        when( sensorViewModel.syncStatus.value ) {
                                0 -> Text("Sync 시작")
                                1 -> Text("Sync - ${sensorViewModel.syncProgress.value}%")
                                2 -> {
                                        if( sensorViewModel.isMeasuring.value ) {
                                                Text("측정 종료")
                                        } else {
                                                Text("측정 시작")
                                        }
                                }
                        }
                }

                // 캘리브레이션을 위한 TextField 추가
                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                        value = minPeakHeight.toString(),
                        onValueChange = {
                                // 값이 변경될 때 Double 형태로 변환하고 상태를 업데이트합니다.
                                try {
                                        minPeakHeight = it.toDouble()
                                } catch (e: NumberFormatException) {
                                        // 숫자 외의 다른 값이 입력되면 예외를 처리합니다.
                                }
                        },
                        label = { Text("Min Peak Height") },
                        modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                        value = minPeakDistance.toString(),
                        onValueChange = {
                                // 값이 변경될 때 Int 형태로 변환하고 상태를 업데이트합니다.
                                try {
                                        minPeakDistance = it.toInt()
                                } catch (e: NumberFormatException) {
                                        // 숫자 외의 다른 값이 입력되면 예외를 처리합니다.
                                }
                        },
                        label = { Text("Min Peak Distance") },
                        modifier = Modifier.fillMaxWidth()
                )

//                FilePickerDialog(context = context, resultViewModel)
//                DisplayResults(resultViewModel)
        }
}
