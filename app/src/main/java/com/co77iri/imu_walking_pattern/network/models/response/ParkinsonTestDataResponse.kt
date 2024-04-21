package com.co77iri.imu_walking_pattern.network.models.response

import com.google.gson.annotations.SerializedName

data class ParkinsonTestDataResponse(
    @SerializedName("result") val result: ParkinsonTestData
) : BaseReponse()
