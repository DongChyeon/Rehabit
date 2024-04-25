package com.co77iri.imu_walking_pattern.network.repository

import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.models.Gender
import com.co77iri.imu_walking_pattern.network.models.request.PostClinicalPatientRequest
import com.co77iri.imu_walking_pattern.network.models.response.BaseReponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestData
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

interface PatientRepository {

    fun postParksonTestData(
        patientId: Int,
        testDateAndTime: String,
        testDuration: Double,
        parkinsonStage: Int,
        averageSpeed: Double,
        cadence: Int,
        gaitCycle: Double,
        totalSteps: Int,
        leftSteps: Int,
        rightSteps: Int,
        strideLength: Double,
        leftStrideLength: Double,
        rightStrideLength: Double,
        stepLength: Double,
        leftStepLength: Double,
        rightStepLength: Double,
        csvFileLeft: File,
        csvFileRight: File
    ): Flow<ApiResult<BaseReponse>>

    fun getParkinsonTestDataList(
        patientId: Int,
        pageNumber: Int,
        pageSize: Int = 10
    ): Flow<ApiResult<ParkinsonTestDataListResponse>>

    fun postClinicalPatient(
        emrPatientNumber: String,
        height: String,
        birthYear: String,
        gender: Gender
    ): Flow<ApiResult<BaseReponse>>

    fun getClinicalPatientList(
        pageNumber: Int,
        pageSize: Int = 10
    ): Flow<ApiResult<ClinicalPatientListResponse>>

    fun getClinicalPatientByEmrPatientNumber(
        emrPatientNumber: String
    ): Flow<ApiResult<ClinicalPatientResponse>>

    fun getParkinsonTestDataById(
        parkinsonTestDataId: Int
    ): Flow<ApiResult<ParkinsonTestDataResponse>>

}