package com.co77iri.imu_walking_pattern.ui.profile

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.co77iri.imu_walking_pattern.models.ProfileData
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class ProfileLegacyViewModel: ViewModel() {
    var profiles by mutableStateOf(listOf<ProfileData>())
    var selectedProfile by mutableStateOf<ProfileData?>(null)
//        private set

    fun loadProfiles(context: Context) {
        val profilesDir = File(context.filesDir, "profiles")
        if( !profilesDir.exists() ) {
            profilesDir.mkdirs()
        }

        val profileFiles = profilesDir.listFiles { _, name ->
            name.endsWith(".json")
        }

        val tmpProfiles = mutableListOf<ProfileData>()
        profileFiles?.forEach { file ->
            val content = file.readText()
            val profile = Json.decodeFromString<ProfileData>(content)
            tmpProfiles.add(profile)
        }

        profiles = tmpProfiles.toList()

        Log.d("test", profiles.toString())
    }

    fun saveProfile(context: Context, profile: ProfileData): ProfileData {
//        val fileName = "${profile.name}_${(1000..9999).random().toString()}.json"
        val generatedFilename = if( profile.filename.isEmpty() ) {
            "${profile.name}_${(1000..9999).random().toString()}.json"
        } else {
            profile.filename
        }

        val file = File(context.filesDir.resolve("profiles"), generatedFilename)
        file.writeText(Json.encodeToString(profile.copy(filename = generatedFilename)))

        return profile.copy(filename = generatedFilename)
    }

    fun saveProfiles(context: Context) {
        val file = File(context.filesDir, "profiles.json")
//        val content = Json.encodeToString(profiles)
        val content = Json.encodeToString(ListSerializer(ProfileData.serializer()), profiles)
        file.writeText(content)
    }

    fun addProfile(profile: ProfileData) {
        profiles = profiles + profile
    }

    fun deleteProfile(profile: ProfileData) {
        profiles = profiles.filter { it != profile }
    }

    fun updateProfile(context: Context, oldProfile: ProfileData, newProfile: ProfileData) {
        deleteProfile(oldProfile)
        addProfile(newProfile)
        saveProfiles(context)
    }
}