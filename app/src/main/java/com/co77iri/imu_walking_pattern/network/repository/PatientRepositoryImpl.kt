package com.co77iri.imu_walking_pattern.network.repository

import android.content.Context
import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.datasource.PatientDataSource
import com.co77iri.imu_walking_pattern.network.models.Gender
import com.co77iri.imu_walking_pattern.network.models.request.PostClinicalPatientRequest
import com.co77iri.imu_walking_pattern.network.models.response.BaseReponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataResponse
import com.co77iri.imu_walking_pattern.network.service.PatientService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val patientDataSource: PatientDataSource
) : PatientRepository {

    override fun postParksonTestData(
        patientId: Int,
        testDateAndTime: String,
        testDuration: Double,
        parkinsonStage: Int,
        csvFileLeft: File,
        csvFileRight: File
    ): Flow<ApiResult<BaseReponse>> {
        val jsonObjectBuilder = JSONObject().apply {
            put("testDateAndTime", testDateAndTime)
            put("testDuration", testDuration)
            put("parkinsonStage", parkinsonStage)
        }

        val requestFile_l = csvFileLeft.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val requestFile_r = csvFileRight.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        val body_l = MultipartBody.Part.createFormData("csvFileLeft", csvFileLeft.name, requestFile_l)
        val body_r = MultipartBody.Part.createFormData("csvFileRight", csvFileRight.name, requestFile_r)

        val jsonData = jsonObjectBuilder.toString().toRequestBody("application/json".toMediaType())

        return patientDataSource.postParksonTestData(patientId, jsonData, body_l, body_r)
    }

    override fun getParkinsonTestDataList(
        patientId: Int,
        pageNumber: Int,
        pageSize: Int
    ) = patientDataSource.getParkinsonTestDataList(patientId, pageNumber, pageSize)

    override fun postClinicalPatient(
        emrPatientNumber: String,
        height: String,
        birthYear: String,
        gender: Gender
    ) = patientDataSource.postClinicalPatient(
        PostClinicalPatientRequest( emrPatientNumber, height, birthYear, gender)
    )

    override fun getClinicalPatientList(
        pageNumber: Int,
        pageSize: Int
    ) = patientDataSource.getClinicalPatientList(pageNumber, pageSize)

    override fun getClinicalPatientByEmrPatientNumber(
        emrPatientNumber: String
    ) = patientDataSource.getClinicalPatientByEmrPatientNumber(emrPatientNumber)

    override fun getParkinsonTestDataById(
        parkinsonTestDataId: Int
    ) = patientDataSource.getParkinsonTestDataById(parkinsonTestDataId)

}