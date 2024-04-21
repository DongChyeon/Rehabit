package com.co77iri.imu_walking_pattern.network.models.response

import com.co77iri.imu_walking_pattern.network.models.Gender
import com.google.gson.annotations.SerializedName

data class ClinicalPatientListResponse(
    @SerializedName("result") val result: ClinicalPatientListResult
) : BaseReponse()

data class ClinicalPatientListResult(
    @SerializedName("content") val content: List<ClinicalPatient>,
    @SerializedName("last") val last: Boolean,
    @SerializedName("first") val first: Boolean
)

data class ClinicalPatient(
    @SerializedName("clinicalPatientId") val clinicalPatientId: Int,
    @SerializedName("emrPatientNumber") val emrPatientNumber: String,
    @SerializedName("height") val height: String,
    @SerializedName("birthYear") val birthYear: String,
    @SerializedName("gender") val gender: Gender
)