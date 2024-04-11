package com.co77iri.imu_walking_pattern

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.co77iri.imu_walking_pattern.utils.Utils.isBluetoothAdapterEnabled
import com.co77iri.imu_walking_pattern.utils.Utils.isLocationPermissionGranted
import com.co77iri.imu_walking_pattern.utils.Utils.requestEnableBluetooth
import com.co77iri.imu_walking_pattern.utils.Utils.requestLocationPermission
import com.co77iri.imu_walking_pattern.viewmodels.BluetoothViewModel
import com.co77iri.imu_walking_pattern.viewmodels.ProfileViewModel
import com.co77iri.imu_walking_pattern.viewmodels.ResultViewModel
import com.co77iri.imu_walking_pattern.viewmodels.SensorViewModel
import com.co77iri.imu_walking_pattern.views.MenuSelectScreen
import com.co77iri.imu_walking_pattern.views.ProfileScreen
import com.co77iri.imu_walking_pattern.views.AddProfileScreen
import com.co77iri.imu_walking_pattern.views.OldCsvSelectScreen
import com.co77iri.imu_walking_pattern.views.CsvResultScreen
import com.co77iri.imu_walking_pattern.views.CsvSelectScreen
import com.co77iri.imu_walking_pattern.views.SensorSettingScreen
import com.co77iri.imu_walking_pattern.views.SensorMeasureScreen
import com.co77iri.imu_walking_pattern.views.SensorSyncScreen
import com.xsens.dot.android.sdk.XsensDotSdk
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    private val bluetoothViewModel: BluetoothViewModel by viewModels()
    private val sensorViewModel: SensorViewModel by viewModels()
    private val resultViewModel: ResultViewModel by viewModels()
//    private val profileViewModelOld: ProfileViewModel_old by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {
            createDirectory()
            profileViewModel.loadProfiles(this@MainActivity)
            delay(100)
//            profileViewModelOld.scanProfilesDirectory(filesDir)
        }

        checkBluetoothAndPermission()
        registerReceiver(bluetoothStateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        initXsensSdk()

        setContent {
            MaterialTheme {
                AppNavigator(
                    this@MainActivity,
                    bluetoothViewModel,
                    sensorViewModel,
                    resultViewModel,
                    profileViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStateReceiver)
    }

    private fun createDirectory() {
        val appDirectory = filesDir
        val profilesDirectory = File(appDirectory, "profiles")
        val notUploadedDirectory = File(appDirectory, "not_uploaded")
        val uploadedDirectory = File(appDirectory, "uploaded")

        if( !profilesDirectory.exists() ) {
            profilesDirectory.mkdir()
        }

        if( !notUploadedDirectory.exists() ) {
            notUploadedDirectory.mkdir()
        }

        if( !uploadedDirectory.exists() ) {
            uploadedDirectory.mkdir()
        }
    }

    private val bluetoothStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null) {
                if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when ( intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) ) {
                        BluetoothAdapter.STATE_OFF -> bluetoothViewModel.updateBluetoothEnableState(false)
                        BluetoothAdapter.STATE_ON -> bluetoothViewModel.updateBluetoothEnableState(true)
                    }
                }
            }
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            Log.d("test006", "${it.key} = ${it.value}")
        }
    }

    private fun initXsensSdk() {
//        XsensDotSdk.setDebugEnabled(true)
        XsensDotSdk.setReconnectEnabled(true)
    }

    private fun checkBluetoothAndPermission(): Boolean {
        val isBluetoothEnabled = if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else isBluetoothAdapterEnabled(this)

        val isPermissionGranted = isLocationPermissionGranted(this)
        if( isBluetoothEnabled ) {
            if( !isPermissionGranted ) requestLocationPermission(this, REQUEST_PERMISSION_LOCATION)
        } else {
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                )
            } else {
                requestEnableBluetooth(this, REQUEST_ENABLE_BLUETOOTH)
            }
        }
        val status = isBluetoothEnabled && isPermissionGranted
//        Log.i(TAG, "checkBluetoothAndPermission() - $status")
        bluetoothViewModel.updateBluetoothEnableState(status)

        return status
    }
    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 1001
        private const val REQUEST_PERMISSION_LOCATION = 1002
    }
}

@Composable
fun AppNavigator(context: Context, bluetoothViewModel: BluetoothViewModel,
                 sensorViewModel: SensorViewModel, resultViewModel: ResultViewModel,
                 profileViewModel: ProfileViewModel
) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "profile") { // 테스트
//    NavHost(navController, startDestination = "csv_select") { // 테스트
        composable("profile") { ProfileScreen(navController, profileViewModel) } // 프로필 선택 페이지 -> 프로필 선택하면 menu_select로 이동
        composable("add_profile") { AddProfileScreen(context, navController, profileViewModel) } // 프로필 추가 페이지
        composable("menu_select") { MenuSelectScreen(navController, profileViewModel) } //
        composable("csv_select") { CsvSelectScreen(navController, resultViewModel) }// 검사결과보기 페이지 -> csv 선택하는 화면 + 업로드 기능 추가
        composable("old_csv_select") { OldCsvSelectScreen(navController) } // 검사결과보기 페이지 -> csv 선택하는 화면
        /* Todo - CsvSelectScreen
            내부 저장소에서 결과 csv 스캔 후 카드 모양으로 불러오기
            → 추후에 측정 결과 저장할 때 파일 이름 지을 수 있도록 ViewModel 함수 변경
         */
        composable("csv_result") { CsvResultScreen(context, navController, resultViewModel, profileViewModel) } // 검사결과보기 페이지 -> csv 선택 후 자세한 정보
        /* Todo - CsvResultScreen
            결과 볼때 ViewModel에서 정보 추출하는것 만들기
         */
        composable("sensor_setting") { SensorSettingScreen(navController, bluetoothViewModel, sensorViewModel) } // 센서 연결 및 이름 선택
        composable("sensor_sync") { SensorSyncScreen(navController, sensorViewModel) }       // 센서 싱크 (100% 되면 자동으로 sensor_measure 페이지로 이동
        composable("sensor_measure") { SensorMeasureScreen(navController, sensorViewModel, resultViewModel) } //
    }
}
