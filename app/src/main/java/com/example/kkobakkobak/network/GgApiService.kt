package com.example.kkobakkobak.network

import com.example.kkobakkobak.data.model.Ggmindmedinst
import com.example.kkobakkobak.data.model.MedicationLog
import retrofit2.Response
import retrofit2.http.*

interface GgApiService {

    @GET("getInpatientStatus")
    suspend fun getInpatientStatus(
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<Ggmindmedinst>

    // --- 투약 기록 관련 서버 API (예시 경로) ---

    @POST("medication/log")
    suspend fun saveMedicationLog(
        @Header("Authorization") token: String,
        @Body log: MedicationLog
    ): Response<MedicationLog>

    @GET("medication/logs")
    suspend fun getMedicationLogs(
        @Header("Authorization") token: String
    ): Response<List<MedicationLog>>
}