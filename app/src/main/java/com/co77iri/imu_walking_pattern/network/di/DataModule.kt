package com.co77iri.imu_walking_pattern.network.di

import com.co77iri.imu_walking_pattern.network.datasource.PatientDataSource
import com.co77iri.imu_walking_pattern.network.datasource.PatientDataSourceImpl
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import com.co77iri.imu_walking_pattern.network.repository.PatientRepositoryImpl
import com.co77iri.imu_walking_pattern.network.service.PatientService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providesPatientDataSource(
        patientService: PatientService
    ): PatientDataSource =
        PatientDataSourceImpl(patientService)

    @Provides
    @Singleton
    fun providesPatientRepository(
        patientDataSource: PatientDataSource
    ): PatientRepository =
        PatientRepositoryImpl(patientDataSource)

}