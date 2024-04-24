package com.co77iri.imu_walking_pattern.network.service

import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.models.request.PostClinicalPatientRequest
import com.co77iri.imu_walking_pattern.network.models.response.BaseReponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ClinicalPatientResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataListResponse
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestDataResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PatientService {

    @Multipart
    @POST("v1/clinicalpatients/{patientId}/parkinsontestdatas")
    suspend fun postParkinsonTestData(
        @Path("patientId") patientId: Int,
        @Part("jsonData") jsonData: RequestBody,
        @Part csvFileLeft: MultipartBody.Part,
        @Part csvFileRight: MultipartBody.Part
    ): ApiResult<BaseReponse>

    @GET("v1/clinicalpatients/{patientId}/parkinsontestdatas")
    suspend fun getParkinsonTestDataList(
        @Path("patientId") patientId: Int,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResult<ParkinsonTestDataListResponse>

    @POST("v1/clinicalpatients")
    suspend fun postClinicalPatient(
        @Body postClinicalPatientRequest: PostClinicalPatientRequest
    ): ApiResult<BaseReponse>

    @GET("v1/clinicalpatients")
    suspend fun getClinicalPatientList(
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResult<ClinicalPatientListResponse>

    @GET("v1/clinicalpatients/{emrPatientNumber}")
    suspend fun getClinicalPatientByEmrPatientNumber(
        @Path("emrPatientNumber") emrPatientNumber: String
    ): ApiResult<ClinicalPatientResponse>

    @GET("v1/parkinsontestdatas/{parkinsonTestDataId}")
    suspend fun getParkinsonTestDataById(
        @Path("parkinsonTestDataId") parkinsonTestDataId: Int
    ): ApiResult<ParkinsonTestDataResponse>
}