package com.co77iri.imu_walking_pattern.network.models.response

import com.google.gson.annotations.SerializedName

open class BaseReponse(
    @SerializedName("message") val message: String = ""
)
