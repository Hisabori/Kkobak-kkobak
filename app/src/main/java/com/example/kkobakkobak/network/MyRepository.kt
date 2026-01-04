package com.example.kkobakkobak.network

import com.example.kkobakkobak.BuildConfig
import com.example.kkobakkobak.data.model.Ggmindmedinst
import com.example.kkobakkobak.data.model.Row
import retrofit2.Response

class MyRepository {
    private val client: GgApiService = RetrofitClient.apiService

    suspend fun fetchData(): List<Row> {
        val apiKey = BuildConfig.GG_API_KEY
        try {
            val response: Response<Ggmindmedinst> = client.getInpatientStatus(
                apiKey = apiKey,
                page = 1,
                perPage = 10
            )
            if (response.isSuccessful) {
                // 💡 filterIsInstance를 사용해 확실하게 List<Row> 타입을 보장
                return response.body()?.row?.filterIsInstance<Row>() ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }
}