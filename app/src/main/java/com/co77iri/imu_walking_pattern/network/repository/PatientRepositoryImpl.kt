package com.co77iri.imu_walking_pattern.network.repository

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
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PatientRepositoryImpl @Inject constructor(
    private val patientDataSource: PatientDataSource
) : PatientRepository {

    override fun postParksonTestData(
        patientId: Int,
        jsonData: RequestBody,
        csvFileLeft: MultipartBody.Part,
        csvFileRight: MultipartBody.Part
    ) = patientDataSource.postParksonTestData(patientId, jsonData, csvFileLeft, csvFileRight)

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