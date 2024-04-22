package com.co77iri.imu_walking_pattern.ui.component

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.co77iri.imu_walking_pattern.ui.sensor.BluetoothViewModel
import com.co77iri.imu_walking_pattern.ui.sensor.SensorViewModel

@Composable
fun ScanResultCard(btViewModel: BluetoothViewModel, sensorViewModel: SensorViewModel, device: BluetoothDevice, isConnected: Boolean) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                if (btViewModel.isScanning.value) {
                    btViewModel.stopScan()
                }

                sensorViewModel.connectSensor(device)
            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                color = Color.Black,
                text = device.address,
                fontSize = 18.sp,
            )

            Row(modifier = Modifier) {
                Box(
                    modifier = Modifier
                        .size(30.dp,30.dp)
                ) {
                    if( isConnected ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "기기 연결여부",
                            modifier = Modifier
                                .size(30.dp, 30.dp),
                            tint = Color.Green
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "기기 연결여부",
                            modifier = Modifier
                                .size(30.dp, 30.dp),
                            tint = Color.LightGray
                        )
                    }

                }

                Box(
                    modifier = Modifier
                        .size(30.dp, 30.dp)
                        .padding(start = 10.dp)
                        .clickable {
                            if (isConnected) {
                                sensorViewModel.disconnectSensor(device.address)
                                sensorViewModel.removeDevice(device.address)
                            }
                        }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "기기 연결해제",
                        modifier = Modifier
                            .size(30.dp, 30.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}