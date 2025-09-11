package com.example.kkobakkobak.network

import com.example.kkobakkobak.BuildConfig
import com.example.kkobakkobak.data.model.Ggmindmedinst
import com.example.kkobakkobak.network.RetrofitClient

class MyRepository {

    suspend fun feachdata(): Ggmindmedinst {
        return RetrofitClient.apiService.getInpatientStatus(
            apiKey = BuildConfig.GG_API_KEY
        )
    }

    /*
    old_code

    suspend fun feachdata() {
        return GgApiService.getInpatientStatus {

            //buildconfig (api키 전달)
            apiKey = com.example.kkobakkobak.BuildConfig.GG_API_KEY

        }
    }

     */

}