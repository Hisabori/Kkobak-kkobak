package com.example.kkobakkobak.network

import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://openapi.gg.go.kr/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
    }

    fun getInstance(): Retrofit {
        return retrofit
    }

    // Unresolved reference 'KkobakService' 해결: 프로젝트에 있는 GgApiService로 연결
    val apiService: GgApiService by lazy {
        retrofit.create(GgApiService::class.java)
    }
}