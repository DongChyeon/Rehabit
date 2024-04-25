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
    @SerializedName("parkinsonStage") val parkinsonStage: Int,
    @SerializedName("averageSpeed") val averageSpeed: Double,
    @SerializedName("cadence") val cadence: Int,
    @SerializedName("gaitCycle") val gaitCycle: Double,
    @SerializedName("totalSteps") val totalSteps: Int,
    @SerializedName("leftSteps") val leftSteps: Int,
    @SerializedName("rightSteps") val rightSteps: Int,
    @SerializedName("strideLength") val strideLength: Double,
    @SerializedName("leftStrideLength") val leftStrideLength: Double,
    @SerializedName("rightStrideLength") val rightStrideLength: Double,
    @SerializedName("stepLength") val stepLength: Double,
    @SerializedName("leftStepLength") val leftStepLength: Double,
    @SerializedName("rightStepLength") val rightStepLength: Double,
    @SerializedName("csvFileUrlLeft") val csvFileUrlLeft: String,
    @SerializedName("csvFileUrlRight") val csvFileUrlRight: String
)