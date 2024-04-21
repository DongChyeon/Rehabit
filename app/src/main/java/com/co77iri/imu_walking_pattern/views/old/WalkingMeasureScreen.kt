package com.co77iri.imu_walking_pattern.views.old

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ui.component.CustomTopAppBar
import com.co77iri.imu_walking_pattern.viewmodels.ResultViewModel
import com.co77iri.imu_walking_pattern.viewmodels.SensorViewModel
import com.xsens.dot.android.sdk.models.XsensDotDevice

@Composable
fun WalkingMeasureScreen(
    navController: NavController,
    sensorViewModel: SensorViewModel,
    resultViewModel: ResultViewModel
) {
    val context = LocalContext.current
//    var isMeasuring by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CustomTopAppBar(titleText = "보행 측정", navController = navController) },
        bottomBar = {
            // 측정 시작 버튼
            if( !sensorViewModel.isMeasuring.value ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val connSensors: ArrayList<XsensDotDevice> =
                                ArrayList(sensorViewModel.sensorList)

                            sensorViewModel.setAllDeviceMeasurement(connSensors)
                            sensorViewModel.createFiles(connSensors)
                            Toast.makeText(context, "측정이 시작됩니다", Toast.LENGTH_SHORT)

                            // ! startMeasuring
                            sensorViewModel.isMeasuring.value = true
                            sensorViewModel.setMeasurement(sensorViewModel.isMeasuring.value)
                        },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        text = "보행측정 시작",
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
                            Toast.makeText(context, "측정이 종료됩니다.", Toast.LENGTH_SHORT)

                            // ! startMeasuring
                            sensorViewModel.isMeasuring.value = false
                            sensorViewModel.closeFiles()
                            sensorViewModel.setMeasurement(sensorViewModel.isMeasuring.value)

                            // TODO 다음 화면으로 넘어가기?
                            resultViewModel.updateCSVDataFromFile(sensorViewModel.leftSensorFileName!!)
                            resultViewModel.updateCSVDataFromFile(sensorViewModel.rightSensorFileName!!)
                            navController.navigate("analysis_screen")
                        },
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        text = "보행측정 종료",
                        fontSize = 18.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.3f))
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("측정 시작 화면")
        }

    }
}