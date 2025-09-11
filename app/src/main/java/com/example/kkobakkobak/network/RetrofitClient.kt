package com.example.kkobakkobak.network

import com.example.kkobakkobak.data.network.GgApiService
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitClient{

    //요청 타깃 url
    private const val BASE_URL = "https://openapi.gg.go.kr/"

    //retrofit 객체 생성
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)


            //xml data converter
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
    }

    val apiService: GgApiService by lazy {
        retrofit.create(GgApiService::class.java)
    }

}