package com.co77iri.imu_walking_pattern.ui.component

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException



fun UploadScreen(selectedFiles: List<File>) {
    val client = OkHttpClient()
    val url = "http://tantan2.com:8000/upload"

    val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)

    selectedFiles.forEach { file ->
        val fileBody = file.asRequestBody("text/csv".toMediaTypeOrNull())
        requestBody.addFormDataPart("csvFiles", file.name, fileBody)
    }

    val request = Request.Builder().url(url).post(requestBody.build()).build()

    client.newCall(request).execute().use { response ->
        if(!response.isSuccessful) throw IOException("Unexpected code $response")
    }
}