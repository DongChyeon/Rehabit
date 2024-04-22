package com.co77iri.imu_walking_pattern.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.co77iri.imu_walking_pattern.ui.sensor.BluetoothViewModel

@Composable
fun SensorScanCard(
    btViewModel: BluetoothViewModel
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable {
                if (!btViewModel.isScanning.value) {
                    btViewModel.startScan()
//                                        showDialog = true
                } else {
                    btViewModel.stopScan()
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                color = Color.Black,
                text = "기기 스캔하기",
                fontSize = 18.sp,
                modifier = Modifier
            )
            Box(
                contentAlignment = Alignment.Center

            ) {
                Switch(
                    checked = btViewModel.isScanning.value,
                    onCheckedChange = {
                        if (!btViewModel.isScanning.value) {
                            btViewModel.startScan()
//                                        showDialog = true
                        } else {
                            btViewModel.stopScan()
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Green,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray,
                        uncheckedBorderColor = Color.LightGray,
                    )
                )
            }
        }
    }
}