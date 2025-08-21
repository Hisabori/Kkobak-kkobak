package com.example.kkobakkobak.ui.path

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalApiService {
    @GET("v2/local/search/keyword.json")
    fun searchPlaces(
        @Header("Authorization") apiKey: String,           // "KakaoAK {REST_API_KEY}"
        @Query("query") query: String,
        @Query("page") page: Int? = null
    ): Call<PlaceResponse>
}
