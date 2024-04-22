package com.co77iri.imu_walking_pattern.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
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
import com.co77iri.imu_walking_pattern.ui.sensor.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice

@Composable
fun ConnectedDeviceCard(
    // Todo : sensorViewModel은 나중에 이름변경 등 수행할때 필요
    sensorViewModel: SensorViewModel, device: XsensDotDevice
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE2E2E2)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
//            .clickable {
//
//            }

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 25.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if( device.tag == "") {
                    Text(
                        color = Color.Black,
                        text = "기기 이름을 설정해 주세요.",
                        fontSize = 20.sp,
                    )
                } else {
                    Text(
                        color = Color.Black,
                        text = device.tag,
                        fontSize = 20.sp,
                    )
                }
                Text(
                    color = Color(0xFF7D7E81),
                    text = device.address,
                    fontSize = 16.sp,
                )
            }

            Row(modifier = Modifier) {
                /* Todo - 연결된 기기 카드
                    연결해제 버튼 대신 이름 변경 버튼 만들기
                 */
                Box(
                    modifier = Modifier
                        .size(30.dp, 30.dp)
                        .padding(start = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "기기 이름변경",
                        modifier = Modifier
                            .size(30.dp, 30.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}