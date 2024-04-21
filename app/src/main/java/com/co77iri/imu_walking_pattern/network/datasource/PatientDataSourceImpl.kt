package com.co77iri.imu_walking_pattern.network.datasource

import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.models.request.PostClinicalPatientRequest
import com.co77iri.imu_walking_pattern.network.models.response.BaseReponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataResponse
import com.co77iri.imu_walking_pattern.network.service.PatientService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class PatientDataSourceImpl @Inject constructor(
    private val patientService: PatientService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PatientDataSource {

    override fun postParksonTestData(
        patientId: Int,
        jsonData: RequestBody,
        csvFileLeft: MultipartBody.Part,
        csvFileRight: MultipartBody.Part
    ) = flow {
        emit(patientService.postParkinsonTestData(patientId, jsonData, csvFileLeft, csvFileRight))
    }.flowOn(ioDispatcher)

    override fun getParkinsonTestDataList(
        patientId: Int,
        pageNumber: Int,
        pageSize: Int
    ) = flow {
        emit(patientService.getParkinsonTestDataList(patientId, pageNumber, pageSize))
    }.flowOn(ioDispatcher)

    override fun postClinicalPatient(
        postClinicalPatientRequest: PostClinicalPatientRequest
    ) = flow {
        emit(patientService.postClinicalPatient(postClinicalPatientRequest))
    }.flowOn(ioDispatcher)

    override fun getClinicalPatientList(
        pageNumber: Int,
        pageSize: Int
    ) = flow {
        emit(patientService.getClinicalPatientList(pageNumber, pageSize))
    }.flowOn(ioDispatcher)

    override fun getClinicalPatientByEmrPatientNumber(
        emrPatientNumber: String
    ) = flow {
        emit(patientService.getClinicalPatientByEmrPatientNumber(emrPatientNumber))
    }.flowOn(ioDispatcher)

    override fun getParkinsonTestDataById(
        parkinsonTestDataId: Int
    ) = flow {
        emit(patientService.getParkinsonTestDataById(parkinsonTestDataId))
    }.flowOn(ioDispatcher)

}