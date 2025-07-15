package com.example.kkobakkobak.ui.path

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApiService {
    @GET("v2/local/search/keyword.json")
    fun keywordSearch(
        @Header("Authorization") key: String,
        @Query("query") query: String,
        @Query("size") size: Int = 1
    ): Call<PlaceResponse>
}