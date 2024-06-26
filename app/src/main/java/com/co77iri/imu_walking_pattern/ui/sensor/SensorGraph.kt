package com.co77iri.imu_walking_pattern.ui.sensor

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.co77iri.imu_walking_pattern.SENSOR_MEASURE
import com.co77iri.imu_walking_pattern.SENSOR_ROUTE
import com.co77iri.imu_walking_pattern.SENSOR_SETTING
import com.co77iri.imu_walking_pattern.SENSOR_SYNC

fun NavGraphBuilder.sensorGraph(
    navController: NavController,
    sensorViewModel: SensorViewModel
) {
    navigation(
        route = SENSOR_ROUTE,
        startDestination = SENSOR_SETTING
    ) {

        composable(SENSOR_SETTING) {
            // 센서 연결 및 이름 선택 화면
            SensorSettingScreen(
                navController = navController,
                btViewModel = hiltViewModel(),
                sensorViewModel = sensorViewModel
            )
        }

        composable(SENSOR_SYNC) {
            // 센서 싱크 화면 (100% 되면 자동으로 sensor_measure 페이지로 이동)
            SensorSyncScreen(
                navController = navController,
                sensorViewModel = sensorViewModel
            )
        }
    }
}