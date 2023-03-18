package com.isfan17.freshnews.data.network

import com.isfan17.freshnews.helper.Constants.Companion.API_KEY
import com.isfan17.freshnews.helper.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("v2/top-headlines")
    suspend fun getFreshNews(
        @Query("country") countryCode: String = "us",
        @Query("category") category: String = "general",
        @Query("page") pageNumber: Int = 2,
        @Query("apiKey") apiKey: String = API_KEY
    ): NetworkArticleContainer

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 2,
        @Query("apiKey") apiKey: String = API_KEY
    ): NetworkArticleContainer

}

object NewsNetwork {
    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    val news = retrofit.create(NewsService::class.java)
}