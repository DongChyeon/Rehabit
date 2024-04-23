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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.co77iri.imu_walking_pattern.Utils.isBluetoothAdapterEnabled
import com.co77iri.imu_walking_pattern.Utils.isLocationPermissionGranted
import com.co77iri.imu_walking_pattern.Utils.requestEnableBluetooth
import com.co77iri.imu_walking_pattern.Utils.requestLocationPermission
import com.co77iri.imu_walking_pattern.ui.upload.UploadResultViewModel
import com.co77iri.imu_walking_pattern.ui.MenuSelectScreen
import com.co77iri.imu_walking_pattern.ui.upload.UploadResultScreen
import com.co77iri.imu_walking_pattern.ui.csv.CsvSelectScreen
import com.co77iri.imu_walking_pattern.ui.csv.CsvSelectViewModel
import com.co77iri.imu_walking_pattern.ui.profile.profileGraph
import com.co77iri.imu_walking_pattern.ui.sensor.sensorGraph
import com.xsens.dot.android.sdk.XsensDotSdk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {
            createDirectory()
        }

        checkBluetoothAndPermission()
        registerReceiver(bluetoothStateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        initXsensSdk()

        setContent {
            MaterialTheme {
                NavHost()
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
                        BluetoothAdapter.STATE_OFF -> App.isBluetoothEnabled = false
                        BluetoothAdapter.STATE_ON -> App.isBluetoothEnabled = true
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
        App.isBluetoothEnabled = status

        return status
    }
    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 1001
        private const val REQUEST_PERMISSION_LOCATION = 1002
    }
}

@Composable
fun NavHost() {
    val context = LocalContext.current

    val navController = rememberNavController()

    NavHost(navController, startDestination = PROFILE_ROUTE) { // 테스트

        profileGraph(navController = navController)

        sensorGraph(navController =  navController)

        composable(MENU_SELECT) {
            MenuSelectScreen(navController)
        } //

        composable(CSV_SELECT) {
            val csvSelectViewModel: CsvSelectViewModel = hiltViewModel()

            CsvSelectScreen(navController, csvSelectViewModel)
        }// 검사결과보기 페이지 -> csv 선택하는 화면 + 업로드 기능 추가

        composable(route = "${UPLOAD_RESULT}?leftcsv={l_csv}&rightcsv={r_csv}",
            arguments = listOf(
                navArgument("l_csv") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("r_csv") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val uploadResultViewModel: UploadResultViewModel = hiltViewModel()

            UploadResultScreen(
                navController = navController,
                l_csv = it.arguments?.getString("l_csv") ?: "",
                r_csv = it.arguments?.getString("r_csv") ?: "",
                viewModel = uploadResultViewModel
            )
        }

    }
}
