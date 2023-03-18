package com.isfan17.freshnews.data.network

import com.google.gson.annotations.SerializedName

data class NetworkArticleContainer(
    @field:SerializedName("totalResults")
    val totalResults: Int,

    @field:SerializedName("articles")
    val articles: List<NetworkArticle>,

    @field:SerializedName("status")
    val status: String
)

data class NetworkArticle(
    @field:SerializedName("author")
    val author: String?,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("description")
    val description: String?,

    @field:SerializedName("url")
    val url: String,

    @field:SerializedName("urlToImage")
    val urlToImage: String?,

    @field:SerializedName("publishedAt")
    val publishedAt: String,

    @field:SerializedName("source")
    val source: NetworkSource
)

data class NetworkSource(
    @field:SerializedName("name")
    val name: String
)