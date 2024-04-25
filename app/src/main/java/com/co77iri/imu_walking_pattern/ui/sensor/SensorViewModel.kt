package com.co77iri.imu_walking_pattern.ui.sensor

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsens.dot.android.sdk.BuildConfig
import com.xsens.dot.android.sdk.events.XsensDotData
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback
import com.xsens.dot.android.sdk.interfaces.XsensDotSyncCallback
import com.xsens.dot.android.sdk.models.FilterProfileInfo
import com.xsens.dot.android.sdk.models.XsensDotDevice
import com.xsens.dot.android.sdk.models.XsensDotPayload
import com.xsens.dot.android.sdk.models.XsensDotSyncManager
import com.xsens.dot.android.sdk.utils.XsensDotLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MutableCollectionMutableState")
class SensorViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    var sensorList by mutableStateOf<SnapshotStateList<XsensDotDevice>>(mutableStateListOf())
    val isMeasuring: MutableState<Boolean> = mutableStateOf(false)

    var syncStatus: MutableState<Int> = mutableStateOf(0) // 0 Not started , 1 in-progress, 2 done
    var syncProgress: MutableState<Int> = mutableStateOf(0)

//    val connectionChangedDevice = MutableLiveData<XsensDotDevice>()
    val connectionChangedDevice: MutableState<XsensDotDevice?> = mutableStateOf(null)
    val tagChangedDevice = MutableLiveData<XsensDotDevice>()

    private val dataList: ArrayList<HashMap<String, Any?>> = ArrayList<HashMap<String,Any?>>()

    // for Logging
    private val loggerList: MutableList<HashMap<String, Any>> = ArrayList()
    val fileNameList = mutableListOf<String>()
    private var isLogging = false

    var leftSensor: MutableState<XsensDotDevice?> = mutableStateOf(null)
    var rightSensor: MutableState<XsensDotDevice?> = mutableStateOf(null)

    var leftSensorFileName: String? = null
    var rightSensorFileName: String? = null

    // For MPAndroidChart
//    private val _sensorData = MutableStateFlow<List<XsensDotData>>(emptyList())
//    val sensorData: StateFlow<List<XsensDotData>> = _sensorData

    private val _LeftSensorData = MutableStateFlow<List<XsensDotData>>(emptyList())
    val LeftSensorData: StateFlow<List<XsensDotData>> = _LeftSensorData

    private val _RightSensorData = MutableStateFlow<List<XsensDotData>>(emptyList())
    val RightSensorData: StateFlow<List<XsensDotData>> = _RightSensorData



    private var dotSyncCallback = object : XsensDotSyncCallback {
        override fun onSyncingStarted(p0: String?, p1: Boolean, p2: Int) {
            Log.d(TAG, "Sync started!")
            syncStatus.value = 1
        }

        override fun onSyncingProgress(progress: Int, requestCode: Int) {
//            Log.d(TAG, "progress: $progress, requestCode: $requestCode")
            syncProgress.value = progress
        }

        override fun onSyncingResult(address: String, isSuccess: Boolean, requestCode: Int) {
            Log.d(TAG, "address: $address, isSuccess: $isSuccess, requestCode: $requestCode")
        }

        override fun onSyncingDone(syncingResultMap: HashMap<String, Boolean>, isSuccess: Boolean, requestCode: Int) {
            Log.d(TAG, "syncingResultMap :${syncingResultMap.toString()}, isSuccess: $isSuccess, requestCode: $requestCode")
            syncStatus.value = 2
        }

        override fun onSyncingStopped(address: String, isSuccess: Boolean, requestCode: Int) {
            Log.d(TAG, "Syncing stopped!")
        }

    }

    private var dotDeviceCallback = object : XsensDotDeviceCallback {
        override fun onXsensDotConnectionChanged(address: String, state: Int) {
            val device = getSensor(address)
//            if( device != null ) connectionChangedDevice.postValue(device)
            if( device != null ) connectionChangedDevice.value = device

            when( state ) {
                XsensDotDevice.CONN_STATE_DISCONNECTED -> synchronized(this) { removeDevice(address)}
                XsensDotDevice.CONN_STATE_CONNECTING -> {
//                    Log.d(TAG, "Sensor connecting ... ${device?.address}")
                }
                XsensDotDevice.CONN_STATE_CONNECTED -> {
//                    Log.d(TAG, "sensor list... ${sensorList}")
                                    }
                XsensDotDevice.CONN_STATE_RECONNECTING -> {
//                    Log.d(TAG, "Sensor re-connecting ... ${device?.address}")
                }
            }
        }

        override fun onXsensDotServicesDiscovered(address: String, state: Int) {
            Log.i(TAG, "onXsensDotServicesDiscovered() - address = $address, status = $state")
        }

        override fun onXsensDotFirmwareVersionRead(address: String, version: String) {
            Log.i(TAG, "onXsensDotFirmwareVersionRead() - address = $address, version = $version")
        }

        override fun onXsensDotTagChanged(address: String, tag: String) {
            Log.i(TAG, "onXsensDotTagChanged() - address = $address, tag = $tag")

            // The default value of tag is an empty string.
            if (tag != "") {
                val device = getSensor(address)
                if (device != null) tagChangedDevice.postValue(device)
            }
        }

        override fun onXsensDotBatteryChanged(address: String, status: Int, percentage: Int) {
            // This callback function will be triggered in the connection precess.
            Log.i(TAG, "onXsensDotBatteryChanged() - address = $address, status = $status, percentage = $percentage")

            // The default value of status and percentage is -1.
//            if (status != -1 && percentage != -1) {
                // Use callback function instead of LiveData to notify the battery information.
                // Because when user removes the USB cable from housing, this function will be triggered 5 times.
                // Use LiveData will lose some notification.
                // TODO
//                if (mBatteryChangeInterface != null) mBatteryChangeInterface!!.onBatteryChanged(address, status, percentage)
//            }
        }

        override fun onXsensDotDataChanged(address: String, data: XsensDotData) {
            if( address == leftSensor.value!!.address ) {
//                _LeftSensorData.value = _LeftSensorData.value + data
                val updatedData = _LeftSensorData.value.toMutableList()
                if (updatedData.size >= 600) {
                    updatedData.removeAt(0)  // 맨 처음 데이터 삭제
                }
                updatedData.add(data)  // 새로운 데이터 추가
                _LeftSensorData.value = updatedData
            }

            if( address == rightSensor.value!!.address ) {
//                _RightSensorData.value = _RightSensorData.value + data
                val updatedData = _RightSensorData.value.toMutableList()
                if (updatedData.size >= 600) {
                    updatedData.removeAt(0)  // 맨 처음 데이터 삭제
                }
                updatedData.add(data)  // 새로운 데이터 추가
                _RightSensorData.value = updatedData
            }

//            if (freeAcc != null && freeAcc.size >= 3) {
//                val freeAccStr = "FreeAcc_X:${freeAcc[0]}, FreeAcc_Y:${freeAcc[1]}, FreeAcc_Z:${freeAcc[2]}"
//                Log.i(TAG, "Addr:$address, $freeAccStr")
//            }

            var isExist = false
            for( map in dataList) {
                val _address = map["address"] as String?
                if( _address == address ) {
                    map["data"] = data
                    isExist = true
                    break
                }
            }
            if( !isExist ) {
                val map: HashMap<String, Any?> = HashMap<String, Any?>()
                map["address"] = address
                map["tag"] = getTag(address)
                map["data"] = data
                dataList.add(map)
            }
            updateFiles(address, data)
        }

        override fun onXsensDotInitDone(address: String) {
            Log.i(TAG, "onXsensDotInitDone() - address = $address")

            val device = getSensor(address)
            if( device != null ) {
                val profileIndex = device.currentFilterProfileIndex
                val outputRate = device.currentOutputRate

                Log.d("InitDone", "filterProfile: $profileIndex, outputRate: $outputRate")

                if( outputRate != 60 ) {
                    device.setOutputRate(60)
                    Log.d(TAG, "Output rate changed to 60!")
                }
                if( profileIndex != 0 ) {
                    device.setFilterProfile(0)
                    Log.d(TAG, "Filter profile changed to 0 - General")
                }
            }
        }

        override fun onXsensDotButtonClicked(address: String, timestamp: Long) {
            Log.i(TAG, "onXsensDotButtonClicked() - address = $address, timestamp = $timestamp")
        }

        override fun onXsensDotPowerSavingTriggered(address: String) {
            Log.i(TAG, "onXsensDotPowerSavingTriggered() - address = $address")
        }

        override fun onReadRemoteRssi(address: String, rssi: Int) {
            Log.i(TAG, "onReadRemoteRssi() - address = $address, rssi = $rssi")
        }

        override fun onXsensDotOutputRateUpdate(address: String, outputRate: Int) {
            Log.i(TAG, "onXsensDotOutputRateUpdate() - address = $address, outputRate = $outputRate")
        }

        override fun onXsensDotFilterProfileUpdate(address: String, filterProfileIndex: Int) {
            Log.i(TAG, "onXsensDotFilterProfileUpdate() - address = $address, filterProfileIndex = $filterProfileIndex")
        }

        override fun onXsensDotGetFilterProfileInfo(address: String, filterProfileInfoList: ArrayList<FilterProfileInfo>) {
            Log.i(TAG, "onXsensDotGetFilterProfileInfo() - address = " + address + ", size = " + filterProfileInfoList.size)
        }

        override fun onSyncStatusUpdate(address: String, isSynced: Boolean) {
            Log.i(TAG, "onSyncStatusUpdate() - address = $address, isSynced = $isSynced")
        }

    }

    private val syncManager: XsensDotSyncManager = XsensDotSyncManager.getInstance(dotSyncCallback)

    fun initSensorData() {
        isMeasuring.value = false
        _LeftSensorData.value = emptyList()
        _RightSensorData.value = emptyList()
    }

    // MAC Address 리스트에서 XsensDotDevice Object를 가져온다.
    fun getSensor(address: String): XsensDotDevice? {
        val devices = sensorList //
        if( devices != null ) {
            for( device in devices ) {
                if( device.address == address ) return device
            }
        }

        return null
    }

    fun changeTag(device: XsensDotDevice, newTag: String) {
        device.saveDeviceTag(newTag)
    }

    fun connectSensor( device: BluetoothDevice? ) {
        val xsDevice = XsensDotDevice(context, device, dotDeviceCallback)
        addDevice(xsDevice)
        xsDevice.connect()
    }

    fun disconnectSensor(address: String) {
        if( sensorList != null ) { //
            for( device in sensorList!!) {
                if( device.address == address ) {
                    device.disconnect()
                    break
                }
            }
        }
    }


    fun startSync(connSensors: ArrayList<XsensDotDevice>) {
        if( connSensors != null ) {
            connSensors[0].isRootDevice = true

            syncManager.startSyncing(connSensors, SYNCING_REQUEST_CODE)
        }
    }

    fun setAllDeviceMeasurement(connSensors: ArrayList<XsensDotDevice>) {
        for(sensor in connSensors) {
            if( sensor != null ) {
                sensor.measurementMode = XsensDotPayload.PAYLOAD_TYPE_EXTENDED_QUATERNION
            }
            // TODO : deviceDataList 추가하기


        }
    }


    fun setMeasurement(enabled: Boolean) {
        val devices = sensorList
        if( devices != null ) {
            for( device in devices ) {
                if( enabled ) device.startMeasuring() else device.stopMeasuring()
            }
        }
    }

    fun getTag(address: String): String {
        val device = getSensor(address)
        if( device != null ) {
            val tag = device.tag
            return tag ?: device.name
        }
        return ""
    }

    private fun addDevice(xsDevice: XsensDotDevice) {
        val devices = sensorList
        var isExist = false
        for (_xsDevice in devices) {
            if (xsDevice.address == _xsDevice.address) {
                isExist = true
                break
            }
        }
        if (!isExist) {
            devices.add(xsDevice)
            sensorList = devices
        }
    }

    fun removeDevice(address: String) {
        val devices = sensorList

        if (devices != null) {
            synchronized(LOCKER) {
                val iterator = devices.iterator()
                while (iterator.hasNext()) {
                    val device = iterator.next()
                    if (device.address == address) {
                        iterator.remove()
                        sensorList = devices // 갱신된 목록을 다시 저장
                        break
                    }
                }
            }
        }
    }

    // For logging
    fun createFiles(connSensors: ArrayList<XsensDotDevice>) {
        loggerList.clear()

        for( device in connSensors ) {
            val appVersion = BuildConfig.VERSION_NAME
            val fwVersion = device.firmwareVersion
            val address = device.address
            val tag = if( device.tag.isEmpty()) device.name else device.tag

            val myDir = File(context.filesDir, "not_uploaded")

            var fileName = ""

            if (address == leftSensor.value!!.address) {
                fileName = myDir.toString() + File.separator + SimpleDateFormat("yyMMdd_HHmm", Locale.getDefault()).format(Date()) + "_L.csv"
                leftSensorFileName = fileName
            } else if (address == rightSensor.value!!.address) {
                fileName = myDir.toString() + File.separator + SimpleDateFormat("yyMMdd_HHmm", Locale.getDefault()).format(Date()) +  "_R.csv"
                rightSensorFileName = fileName
            }

            Log.d(TAG, "createFiles() - $fileName")
            val logger = XsensDotLogger(
                context, XsensDotLogger.TYPE_CSV, XsensDotPayload.PAYLOAD_TYPE_EXTENDED_QUATERNION,
                fileName, tag, fwVersion, device.isSynced, device.currentOutputRate,
                "General", appVersion, 0
            )

            fileNameList.add(fileName)

            val map = HashMap<String, Any>()
            map["address"] = address
            map["logger"] = logger
            loggerList.add(map)
        }

        isLogging = true
    }

    fun createCalibrationFiles(connSensors: ArrayList<XsensDotDevice>): Array<String> {
        Log.d("test", "createFiles() - connSEnsors : ${connSensors.joinToString()}")
        Log.d("test", "createFiles() - connSEnsors : ${connSensors.toString()}")

        loggerList.clear()
        var caliResults: Array<String> = emptyArray<String>()

        for( device in connSensors ) {
            val appVersion = BuildConfig.VERSION_NAME
            val fwVersion = device.firmwareVersion
            val address = device.address
            val tag = if( device.tag.isEmpty()) device.name else device.tag
            var fileName = ""

            if( context != null ) {
                val dir = context.getExternalFilesDir(null)
                if( dir != null ) {
                    fileName = dir.absolutePath + File.separator + "calibration" + File.separator +
                            SimpleDateFormat("yyMMdd_HHmm", Locale.getDefault()).format(Date()) +
                            "_" + address.takeLast(2) + ".csv"

                    caliResults += fileName
                }
            }
            Log.d(TAG, "createFiles() - $fileName")
            val logger = XsensDotLogger(
                context, XsensDotLogger.TYPE_CSV, XsensDotPayload.PAYLOAD_TYPE_EXTENDED_QUATERNION,
                fileName, tag, fwVersion, device.isSynced, device.currentOutputRate,
                "General", appVersion, 0
            )

            fileNameList.add(fileName)

            val map = HashMap<String, Any>()
            map["address"] = address
            map["logger"] = logger
            loggerList.add(map)
        }

        isLogging = true

        return caliResults
    }

    private fun updateFiles( address: String?, data: XsensDotData? ) {
        for( map in loggerList ) {
            val _address = map["address"] as String?
            if( _address != null ) {
                if( _address == address ) {
                    val logger = map["logger"] as XsensDotLogger?
                    if( logger != null && isLogging ) logger.update(data)
                }
            }
        }
    }

    fun closeFiles() {
        isLogging = false

        for( map in loggerList ) {
            val logger = map["logger"] as XsensDotLogger?
            logger?.stop()
        }
    }

    companion object {
        private val TAG = SensorViewModel::class.java.simpleName
        private val LOCKER = Any()
        private const val SYNCING_REQUEST_CODE = 1001
    }
}