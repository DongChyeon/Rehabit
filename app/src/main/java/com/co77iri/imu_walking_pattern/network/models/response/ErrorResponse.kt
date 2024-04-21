package com.co77iri.imu_walking_pattern.network.models.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("status") val status: Int,
    @SerializedName("error") val error: String,
    @SerializedName("path") val path: String
)
