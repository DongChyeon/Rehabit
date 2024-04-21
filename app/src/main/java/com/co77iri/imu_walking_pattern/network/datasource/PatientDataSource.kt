package com.co77iri.imu_walking_pattern.network.datasource

import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import kotlinx.coroutines.flow.Flow
import com.co77iri.imu_walking_pattern.network.models.request.PostClinicalPatientRequest
import com.co77iri.imu_walking_pattern.network.models.response.BaseReponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface PatientDataSource {
    
    fun postParksonTestData(
        patientId: Int,
        jsonData: RequestBody,
        csvFileLeft: MultipartBody.Part,
        csvFileRight: MultipartBody.Part
    ): Flow<ApiResult<BaseReponse>>
    
    fun getParkinsonTestDataList(
        patientId: Int,
        pageNumber: Int,
        pageSize: Int = 10
    ): Flow<ApiResult<ParkinsonTestDataListResponse>>
    
    fun postClinicalPatient(
        postClinicalPatientRequest: PostClinicalPatientRequest
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