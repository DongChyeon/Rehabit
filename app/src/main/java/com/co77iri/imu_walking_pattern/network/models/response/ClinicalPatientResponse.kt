package com.co77iri.imu_walking_pattern.network.models.response

import com.google.gson.annotations.SerializedName

data class ClinicalPatientResponse(
    @SerializedName("result") val result: ClinicalPatient
) : BaseReponse()