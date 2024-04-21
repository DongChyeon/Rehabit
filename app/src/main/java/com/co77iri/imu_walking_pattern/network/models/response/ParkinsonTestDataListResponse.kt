package com.co77iri.imu_walking_pattern.network.models.response

import com.google.gson.annotations.SerializedName

data class ParkinsonTestDataListResponse(
    @SerializedName("result") val result: ParkinsonTestDataListResult
) : BaseReponse()

data class ParkinsonTestDataListResult(
    @SerializedName("content") val content: List<ParkinsonTestData>,
    @SerializedName("last") val last: Boolean,
    @SerializedName("first") val first: Boolean
)

data class ParkinsonTestData(
    @SerializedName("parkinsonTestDataId") val parkinsonTestDataId: Int,
    @SerializedName("testDateAndTime") val testDateAndTime: String,
    @SerializedName("testDuration") val testDuration: String,
    @SerializedName("parkinsonStage") val parkinsonStage: Int,
    @SerializedName("csvFileUrlLeft") val csvFileUrlLeft: String,
    @SerializedName("csvFileUrlRight") val csvFileUrlRight: String
)