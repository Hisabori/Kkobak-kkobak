package com.example.kkobakkobak.data.network

import com.example.kkobakkobak.data.model.Ggmindmedinst
import retrofit2.http.GET
import retrofit2.http.Query



interface GgApiService {
    @GET("Ggmindmedinst")
    suspend fun getInpatientStatus(
            @Query("KEY") apiKey: String,
        @Query("pIndex") page: Int = 1,
        @Query("pSize") size: Int = 100
    ): Ggmindmedinst


}