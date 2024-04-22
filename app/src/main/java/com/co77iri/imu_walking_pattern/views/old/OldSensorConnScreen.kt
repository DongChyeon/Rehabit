package com.co77iri.imu_walking_pattern.views.old

import android.bluetooth.BluetoothDevice
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ui.component.CustomTopAppBar
import com.co77iri.imu_walking_pattern.ui.sensor.BluetoothViewModel
import com.co77iri.imu_walking_pattern.ui.sensor.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice

@Composable
fun OldSensorConnScreen(
    navController: NavController,
    btViewModel: BluetoothViewModel,
    sensorViewModel: SensorViewModel
) {
    // Blinking Text
    val infiniteTransition = rememberInfiniteTransition()

    // 천천히 색상을 바꾸는 애니메이션
    val animationColor by infiniteTransition.animateColor(
        initialValue = Color.Gray,
        targetValue = Color.Black,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var showRenameDialog by remember { mutableStateOf(false) }
    var deviceToRename by remember { mutableStateOf<XsensDotDevice?>(null) }
    var newTagName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CustomTopAppBar(titleText = "센서 연결", navController = navController) },
        bottomBar = {
            if(sensorViewModel.sensorList.size == 2) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("sensor_setting_screen")

                        },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        text = "센서 설정",
                        fontSize = 18.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!btViewModel.isScanning.value) {
                                btViewModel.startScan()
//                                        showDialog = true
                            } else {
                                btViewModel.stopScan()
                            }
                        },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        text = if (!btViewModel.isScanning.value) {
                            "스캔 시작"
                        } else {
                            "스캔 중지"
                        },
                        fontSize = 18.sp,
                        color = animationColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.3f))
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("스캔 결과", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                for (deviceMap in btViewModel.scannedSensorList) {
                                    val device = deviceMap["device"] as? BluetoothDevice
                                    val state = deviceMap["state"] as? Int

                                    var isExist = false

                                    if (device != null && state != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text("기기 주소 : ${device.address}")
                                            TextButton(onClick = {
                                                if (btViewModel.isScanning.value) {
                                                    btViewModel.stopScan()
                                                }

                                                if (sensorViewModel.sensorList.isNotEmpty()) {
                                                    val it: Iterator<XsensDotDevice> =
                                                        sensorViewModel.sensorList.iterator()
                                                    while (it.hasNext()) {
                                                        val sensorListDevice = it.next()

                                                        if (sensorListDevice.address == device.address) {
                                                            isExist = true
                                                            break
                                                        }
                                                    }
                                                }

                                                if (isExist) {
                                                    sensorViewModel.disconnectSensor(device.address)
                                                    sensorViewModel.removeDevice(device.address)
                                                } else {
                                                    sensorViewModel.connectSensor(device)
                                                }
                                            }) {
                                                Text("연결")
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) { Text("연결된 기기", fontSize = 20.sp) }
                                Spacer(modifier = Modifier.height(12.dp))

                                if (sensorViewModel.sensorList.isNotEmpty()) {
                                    val it: Iterator<XsensDotDevice> =
                                        sensorViewModel.sensorList.iterator()
                                    while (it.hasNext()) {
                                        val device = it.next()

//                                        Text("Address: ${device.address}, 상태: ${device.connectionState}")
                                        Text("기기 주소: ${device.address}")
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
//                                                Text("Address: ${device.address}")
                                            if( device.tag == "" ) {
                                                Text("기기 이름을 설정해 주세요.")
                                            } else {
                                                Text("기기 이름 : ${device.tag}")
                                            }

                                            TextButton(onClick = {
                                                deviceToRename = device
                                                showRenameDialog = true
                                            }) {
                                                Text("이름 변경")
                                            }
                                        }
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            TextButton(onClick = {
                                                sensorViewModel.disconnectSensor(device.address)
                                            }) {
                                                Text("연결 해제")
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                    }
                                }
                            }
                        }
                    }
                }

                if(showRenameDialog) {
                    Dialog(
                        onDismissRequest = { showRenameDialog = false }
                    ) {
                        Column(
                            modifier = Modifier
                                .background(color = Color.White)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("기기 이름 변경", fontSize = 20.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            TextField(
                                value = newTagName,
                                onValueChange = { newTagName = it },
                                label = { Text("새 이름") }
                            )
                            OutlinedButton(onClick = {
                                deviceToRename?.let {
                                    sensorViewModel.changeTag(it, newTagName)
                                }
                                showRenameDialog = false
                            }) {
                                Text("확인")
                            }
                        }
                    }
                }
            }
        }
    }
}