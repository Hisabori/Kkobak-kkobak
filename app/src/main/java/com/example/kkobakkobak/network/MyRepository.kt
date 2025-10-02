package com.example.kkobakkobak.network

import com.example.kkobakkobak.BuildConfig
import com.example.kkobakkobak.data.model.Ggmindmedinst
import com.example.kkobakkobak.data.model.Row
import retrofit2.Response
import com.example.kkobakkobak.network.GgApiService
import kotlin.collections.emptyList // 명시적인 import는 뺐지만, 필요하면 추가 가능

class MyRepository {

    private val client: GgApiService = RetrofitClient.getInstance().create(GgApiService::class.java)

    suspend fun fetchData(): List<Row> {
        val apiKey = BuildConfig.GG_API_KEY

        // try 블록 안에서 초기화하기 위해 var 대신 val로 선언하고 let을 사용하는 방식이 더 깔끔합니다.
        // 여기서는 기존 var response 대신 try 블록 안에서만 사용하겠습니다.

        try {
            var response: Response<Ggmindmedinst> = client.getInpatientStatus(
                apiKey = apiKey,
                page = 1,
                perPage = 10
            )

            // API 호출이 성공했을 때만 데이터를 처리
            if (response.isSuccessful) {
                // response.body()?.row를 List<Row>로 안전하게 캐스팅하고, 실패 시 emptyList<Row>() 반환
                // 이 한 줄로 List<Any> 타입 불일치 문제를 종결합니다.
                val resultList = response.body()?.row as? List<Row>
                return resultList ?: emptyList()
            }

        } catch (e: Exception) {
            // 네트워크 오류 등 예외 발생 시 빈 리스트 반환
            e.printStackTrace()
            return emptyList()
        }

        // API 호출이 성공했지만 응답 코드가 실패했거나 (isSuccessful=false),
        // try/catch 블록에서 처리되지 않은 모든 경우에 빈 리스트 반환
        return emptyList()
    }
}