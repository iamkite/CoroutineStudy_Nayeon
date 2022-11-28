package com.nayeon.coroutinestudy.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadApi {
    @GET
    @Streaming
    suspend fun downloadImage(@Url imgUrl: String): ResponseBody
}
