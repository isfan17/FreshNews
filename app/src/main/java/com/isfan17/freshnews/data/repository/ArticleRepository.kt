package com.isfan17.freshnews.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.isfan17.freshnews.data.local.ArticleDao
import com.isfan17.freshnews.data.local.ArticleEntity
import com.isfan17.freshnews.data.model.Article
import com.isfan17.freshnews.data.network.NewsService
import com.isfan17.freshnews.helper.Constants.Companion.NO_RESULTS_MSG
import com.isfan17.freshnews.helper.CountryCodePreferences
import com.isfan17.freshnews.helper.Result
import kotlinx.coroutines.flow.Flow

class ArticleRepository(
    private val preferences: CountryCodePreferences,
    private val apiService: NewsService,
    private val articleDao: ArticleDao
) {
    // Country Code
    fun getCountryCode(): Flow<String> = preferences.getCountryCode()

    suspend fun saveCountryCode(countryCode: String) {
        preferences.saveCountryCode(countryCode)
    }

    // Fresh News
    suspend fun getFreshNews(
        countryCode: String = "us",
        category: String = "general"
    ): Result<List<Article>> {
        return try {
            Log.d("ArticleRepository", "getFreshNews, countryCode: $countryCode")
            val response = apiService.getFreshNews(countryCode = countryCode, category = category)
            val networkArticles = response.articles
            val articles = networkArticles.map { networkArticle ->
                Article(
                    author = networkArticle.author,
                    title = networkArticle.title,
                    description = networkArticle.description,
                    url = networkArticle.url,
                    urlToImage = networkArticle.urlToImage,
                    publishedAt = networkArticle.publishedAt,
                    source = networkArticle.source.name,
                )
            }
            Result.Success(articles)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    // Bookmarked News
    fun getBookmarkedNews(): LiveData<List<Article>> {
        val articles = Transformations.map(articleDao.getAllArticles()) { articleEntities ->
            articleEntities.map { articleEntity ->
                Article(
                    author = articleEntity.author,
                    title = articleEntity.title,
                    description = articleEntity.description,
                    url = articleEntity.url,
                    urlToImage = articleEntity.urlToImage,
                    publishedAt = articleEntity.publishedAt,
                    source = articleEntity.source,
                )
            }
        }
        return articles
    }

    fun isNewsBookmarked(url: String): LiveData<Boolean> = articleDao.isArticleExists(url)

    suspend fun setNewsBookmark(article: Article, bookmarkAction: Boolean) {
        if (bookmarkAction) {
            val articleEntity = ArticleEntity(
                url = article.url,
                author = article.author,
                title = article.title,
                description = article.description,
                urlToImage = article.urlToImage,
                publishedAt = article.publishedAt,
                source = article.source,
            )
            articleDao.upsert(articleEntity)
        } else {
            articleDao.deleteArticle(article.url)
        }
    }

    // Search News
    suspend fun searchForNews(query: String): Result<List<Article>> {
        return try {
            val response = apiService.searchForNews(searchQuery = query)
            val networkTotalResult = response.totalResults
            val networkArticles = response.articles

            if (networkTotalResult == 0) {
                Result.Error(NO_RESULTS_MSG)
            }
            else
            {
                val articles = networkArticles.map { networkArticle ->
                    Article(
                        author = networkArticle.author,
                        title = networkArticle.title,
                        description = networkArticle.description,
                        url = networkArticle.url,
                        urlToImage = networkArticle.urlToImage,
                        publishedAt = networkArticle.publishedAt,
                        source = networkArticle.source.name,
                    )
                }
                Result.Success(articles)
            }
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    companion object {
        @Volatile
        private var instance: ArticleRepository? = null
        fun getInstance(
            preferences: CountryCodePreferences,
            apiService: NewsService,
            newsDao: ArticleDao
        ): ArticleRepository =
            instance ?: synchronized(this) {
                instance ?: ArticleRepository(preferences, apiService, newsDao)
            }.also { instance = it }
    }
}