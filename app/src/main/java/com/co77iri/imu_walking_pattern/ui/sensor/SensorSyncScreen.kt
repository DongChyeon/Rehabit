package com.co77iri.imu_walking_pattern.ui.sensor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.co77iri.imu_walking_pattern.SENSOR_MEASURE
import com.co77iri.imu_walking_pattern.SENSOR_SYNC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorSyncScreen(
    navController: NavController,
    sensorViewModel: SensorViewModel
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar (
                title = {
                    Text(
                        "센서 동기화 중",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "뒤로가기",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2F3239))
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (sensorViewModel.syncStatus.value < 2) {
                CircularProgressIndicator(
                    modifier = Modifier.size(150.dp)
                )
                Text(
                    "${sensorViewModel.syncProgress.value}%",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 40.sp
                )
            }
        }
    }

    LaunchedEffect(sensorViewModel.syncStatus.value) {
        if (sensorViewModel.syncStatus.value >= 2) {
            navController.navigate(
                SENSOR_MEASURE
            ) {
                popUpTo(SENSOR_SYNC) {
                    inclusive = true
                }
            }
        }
    }
}