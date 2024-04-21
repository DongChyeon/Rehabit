package com.co77iri.imu_walking_pattern.network.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.co77iri.imu_walking_pattern.BuildConfig.BASE_URL
import com.co77iri.imu_walking_pattern.network.adapter.ApiResultCallAdapterFactory
import com.co77iri.imu_walking_pattern.network.adapter.converter.EnumConverterFactory
import com.co77iri.imu_walking_pattern.network.service.PatientService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
            )
            .addNetworkInterceptor(ChuckerInterceptor(context))
            .build()

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(EnumConverterFactory())
            .addCallAdapterFactory(ApiResultCallAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun providesPatientService(retrofit: Retrofit): PatientService =
        retrofit.create(PatientService::class.java)

}