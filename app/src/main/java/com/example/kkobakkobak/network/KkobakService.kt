package com.example.kkobakkobak.network// 수정 시작: app/src/main/java/com/example/kkobakkobak/network/KkobakService.kt (신규 파일)

import android.R
import androidx.work.Operation
import com.example.kkobakkobak.data.model.MedicationLog
import com.example.kkobakkobak.data.model.MoodLog
import com.example.kkobakkobak.ui.history.MedicationHistoryActivity
import org.jetbrains.annotations.Async
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.GET

data class ScheduleItemServer(
    val serverId: String,
    val day: String,
    val time:String,
    val content:String,
    val address:String
)

//server
data class ServerResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

//qr인증
data class QrSession(
    val token :String,
    val expiresAt: Long
)

interface KkobakService{

    //투약관리
    @POST("logs/medication")
    suspend fun  saveMedicationLog(
        @Header("Authorization")token: String,
        @Body log: MedicationLog
    ): ServerResponse<MedicationLog>


    //기분기록
    @POST("logs/mood")
    suspend fun saveMoodLog(
        @Header("Authorization")token:String,
        @Body log: MoodLog
    ): ServerResponse<MoodLog>

    @GET("logs/medication")
    suspend fun getAllMedicationLogs(
        @Header("Authorization")token: String
    ): ServerResponse<List<MedicationLog>>

    @POST("auth/qr_session")
    suspend fun requestQrSession(): ServerResponse<QrSession>

    @GET("schedules")
    suspend fun getSchedules(
        @Header("Authorization")token: String
    ): ServerResponse<List<ScheduleItemServer>>



}

