package com.nayeon.coroutinestudy.api

import com.nayeon.coroutinestudy.Constants
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApi {
    @GET("v1/search/image")
    suspend fun searchImage(
        @Header("X-Naver-Client-Id") clientId: String = Constants.clientId,
        @Header("X-Naver-Client-Secret") clientSecret: String = Constants.clientSecret,
        @Query("query") query: String,
        @Query("display") display: Int? = null,
        @Query("start") start: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("filter") filter: String? = null
    ): ImageSearchResponse
}