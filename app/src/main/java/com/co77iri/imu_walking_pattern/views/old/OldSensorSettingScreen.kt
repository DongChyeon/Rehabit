package com.co77iri.imu_walking_pattern.views.old

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ui.component.CustomTopAppBar
import com.co77iri.imu_walking_pattern.viewmodels.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice

@Composable
fun OldSensorSettingScreen (
    navController: NavController,
    sensorViewModel: SensorViewModel
) {
    var showLeftDropdown by remember { mutableStateOf(false) }
    var showRightDropdown by remember { mutableStateOf(false) }
    var isSyncing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CustomTopAppBar(titleText = "센서 설정", navController = navController) },
        bottomBar = {
            // 왼발 오른발 센서가 모두 선택되면, BottomBar에 Sync 시작 버튼 나오게 하기
            if( sensorViewModel.leftSensor != null && sensorViewModel.rightSensor != null ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val connSensors: ArrayList<XsensDotDevice> =
                                ArrayList(sensorViewModel.sensorList)

                            // ! startSync
                            isSyncing = true
                            sensorViewModel.startSync(connSensors)

//                            navController.navigate("sensor_sync_screen")
                            // 싱크가 완료되면 다음 화면으로 이동
                            // 싱크 완료는 sensorViewModel.syncStatus.value >= 2로 확인.
                        },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        text = "Sync 시작",
                        fontSize = 18.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LaunchedEffect(sensorViewModel.syncStatus.value) {
                if( sensorViewModel.syncStatus.value >= 2 ) {
                    navController.navigate("walking_measure_screen")
                }
            }
        }
    ) { innerPadding ->
        if( !isSyncing ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("왼발", fontSize = 16.sp)

                        Box {
                            TextButton(onClick = { showLeftDropdown = !showLeftDropdown }) {
                                if (sensorViewModel.leftSensor.value == null) {
                                    Text("센서 선택")
                                } else {
                                    Text(sensorViewModel.leftSensor.value!!.address)
                                }
                            }

                            DropdownMenu(
                                expanded = showLeftDropdown,
                                onDismissRequest = { showLeftDropdown = false }) {
                                sensorViewModel.sensorList.forEach { device ->
                                    DropdownMenuItem(
                                        text = { Text(device.address) },
                                        onClick = {
                                            sensorViewModel.leftSensor.value = device
                                            showLeftDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 오른발
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("오른발", fontSize = 16.sp)

                        Box {
                            TextButton(onClick = { showRightDropdown = !showRightDropdown }) {
                                if (sensorViewModel.rightSensor.value == null) {
                                    Text("센서 선택")
                                } else {
                                    Text(sensorViewModel.rightSensor.value!!.address)
                                }
                            }

                            DropdownMenu(
                                expanded = showRightDropdown,
                                onDismissRequest = { showRightDropdown = false }) {
                                sensorViewModel.sensorList.forEach { device ->
                                    DropdownMenuItem(
                                        text = { Text(device.address) },
                                        onClick = {
                                            sensorViewModel.rightSensor.value = device
                                            showRightDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 다음 버튼
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if( sensorViewModel.syncStatus.value < 2) {
                    Text("Sync 진행중\n${sensorViewModel.syncProgress.value}%", fontSize = 24.sp, textAlign = TextAlign.Center)
                }
            }
        }

    }
}