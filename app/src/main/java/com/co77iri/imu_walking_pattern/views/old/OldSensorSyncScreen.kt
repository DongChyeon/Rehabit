package com.co77iri.imu_walking_pattern.views.old

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.ui.component.CustomTopAppBar
import com.co77iri.imu_walking_pattern.viewmodels.SensorViewModel

@Composable
fun OldSensorSyncScreen(
    navController: NavController,
    sensorViewModel: SensorViewModel
) {
    Scaffold(
        topBar = { CustomTopAppBar(titleText = "센서 싱크", navController = navController) },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if( sensorViewModel.syncStatus.value < 2 ) {
                Text("Sync 진행중\n${sensorViewModel.syncProgress.value}%", fontSize = 24.sp, textAlign = TextAlign.Center)
            }
        }
    }
}