package com.co77iri.imu_walking_pattern.network.models.request

import com.co77iri.imu_walking_pattern.network.models.Gender
import com.google.gson.annotations.SerializedName

data class PostClinicalPatientRequest(
    @SerializedName("emrPatientNumber") val emrPatientNumber: String,
    @SerializedName("height") val height: String,
    @SerializedName("birthYear") val birthYear: String,
    @SerializedName("gender") val gender: Gender
)
