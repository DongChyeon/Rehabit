package com.co77iri.imu_walking_pattern

import android.app.Application
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        var selectedProfile: ClinicalPatient? = null
        var isBluetoothEnabled: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
    }

}