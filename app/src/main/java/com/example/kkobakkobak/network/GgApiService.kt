package com.example.kkobakkobak.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import com.example.kkobakkobak.data.model.Ggmindmedinst // 👈 Unresolved reference 해결!

// 이 인터페이스는 MyRepository에서 사용하는 API 함수를 정의합니다.
interface GgApiService {

    // MyRepository.kt에서 호출하는 getInpatientStatus 함수를 정의합니다.
    // 여기서 사용하는 매개변수 이름(apiKey, page, perPage)이 MyRepository와 일치해야 합니다.
    @GET("getInpatientStatus") // 👈 실제 API 엔드포인트로 수정하세요.
    suspend fun getInpatientStatus(
        @Query("apiKey") apiKey: String,
        @Query("page") page: Int,
        @Query("perPage") perPage: Int
    ): Response<Ggmindmedinst> // 👈 문법적으로 깔끔하게 정리

    // 필요한 경우 다른 API 함수도 여기에 추가하세요.
}