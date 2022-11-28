package com.nayeon.coroutinestudy.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object DownloadModule {
    fun createDownloadRetrofit(onAttachmentDownloadUpdate: (Int) -> Unit): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://various.img.url")
            .client(createOkHttpProgressClient(onAttachmentDownloadUpdate))
            .build()
    }

    private fun createOkHttpProgressClient(onAttachmentDownloadUpdate: (Int) -> Unit): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(interceptor)
        builder.addInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            originalResponse.newBuilder()
                .body(originalResponse.body?.let { ProgressResponseBody(it, onAttachmentDownloadUpdate) })
                .build()
        }
        return builder.build()
    }
}
