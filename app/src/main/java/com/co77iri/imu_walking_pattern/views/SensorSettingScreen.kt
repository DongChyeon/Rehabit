package com.co77iri.imu_walking_pattern.views

import android.bluetooth.BluetoothDevice
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.SENSOR_SYNC
import com.co77iri.imu_walking_pattern.ui.component.ConnectedDeviceCard
import com.co77iri.imu_walking_pattern.ui.component.ScanResultCard
import com.co77iri.imu_walking_pattern.ui.component.SensorScanCard
import com.co77iri.imu_walking_pattern.viewmodels.BluetoothViewModel
import com.co77iri.imu_walking_pattern.viewmodels.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorSettingScreen(
    navController: NavController,
    btViewModel: BluetoothViewModel,
    sensorViewModel: SensorViewModel
) {
    val scanningText = remember { mutableStateOf("기기 스캔 중") }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior( rememberTopAppBarState() )

    LaunchedEffect(btViewModel.isScanning.value) {
        while (btViewModel.isScanning.value) {
            scanningText.value = "기기 스캔 중" + ".".repeat((System.currentTimeMillis() / 500 % 4).toInt())
            delay(500) // 0.5초마다 텍스트 업데이트
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
                CenterAlignedTopAppBar (
                    title = {
                        Text(
                            "센서 연결 설정",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = White
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2F3239))

                )
            },
        bottomBar = {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF424651)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable {
                               if( sensorViewModel.sensorList.size != 2 ) {
                                   // ! Toast Msg → 센서가 N개 연결되어 있습니다. 센서를 2개 연결해 주세요.
                               } else {
                                   // Todo -
                                   val connSensors: ArrayList<XsensDotDevice> =
                                       ArrayList(sensorViewModel.sensorList)

                                   sensorViewModel.startSync(connSensors)
                                   navController.navigate(SENSOR_SYNC)
                               }
                    },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            color = White,
                            text = "보행측정 시작",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(start = 18.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(
                    color = Color(0xFFF3F3F3)
                )
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxSize(),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp))
            {
                SensorScanCard(btViewModel)
                Spacer(modifier = Modifier.height(10.dp))
                if( btViewModel.isScanning.value ) {
                    Text(text = scanningText.value)
                } else {
                    Text(text = "기기 스캔 결과")
                }

                // ! 스캔 결과
                for( deviceMap in btViewModel.scannedSensorList ) {
                    val device = deviceMap["device"] as? BluetoothDevice
                    val state = deviceMap["state"] as? Int
                    
                    var isConnected = false // sensorViewmodel에서 기기가 연결되어 있는지 확인

                    if( device != null && state != null) {
                        // sensorViewModel.sensorList를 순회하며 연결되어있는지 여부 체크
                        if(sensorViewModel.sensorList.isNotEmpty()) {
                            val it: Iterator<XsensDotDevice> =
                                sensorViewModel.sensorList.iterator()
                            while(it.hasNext()) {
                                val sensorListDevice = it.next()

                                if( sensorListDevice.address == device.address ) {
                                    isConnected = true
                                    break
                                }
                            }
                        }

                        ScanResultCard(btViewModel, sensorViewModel, device, isConnected)
                    }
                }

                // 연결된 기기
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = "연결된 기기")

                // ! 연결된 기기 목록
                if( sensorViewModel.sensorList.isNotEmpty() ) {
                    val it: Iterator<XsensDotDevice> =
                        sensorViewModel.sensorList.iterator()

                    while( it.hasNext() ) {
                        val device = it.next()

                        ConnectedDeviceCard(sensorViewModel, device)
                    }
                }
            }
        }
    }
}
