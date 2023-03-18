package com.isfan17.freshnews.ui.viewmodels

import androidx.lifecycle.*
import com.isfan17.freshnews.data.model.Article
import com.isfan17.freshnews.data.repository.ArticleRepository
import kotlinx.coroutines.launch
import com.isfan17.freshnews.helper.Result

class NewsViewModel(private val repository: ArticleRepository) : ViewModel() {

    // Country Code
    fun getCountryCode(): LiveData<String> = repository.getCountryCode().asLiveData()

    fun saveCountryCode(newCountryCode: String) {
        viewModelScope.launch {
            repository.saveCountryCode(newCountryCode)
        }
    }

    // Fresh News
    private val _freshNews = MutableLiveData<Result<List<Article>>>()
    val freshNews: LiveData<Result<List<Article>>> = _freshNews

    fun getFreshNews(
        countryCode: String = "us",
        category: String = "general"
    ) {
        viewModelScope.launch {
            _freshNews.value = Result.Loading
            _freshNews.value = repository.getFreshNews(countryCode, category)
        }
    }

    // Bookmarked News
    fun getBookmarkedNews() = repository.getBookmarkedNews()

    fun setNewsBookmark(article: Article, bookmarkAction: Boolean) {
        viewModelScope.launch {
            repository.setNewsBookmark(article, bookmarkAction)
        }
    }

    fun isNewsBookmarked(url: String) = repository.isNewsBookmarked(url)

    // Search News
    private val _searchNews = MutableLiveData<Result<List<Article>>>()
    val searchNews: LiveData<Result<List<Article>>> = _searchNews

    fun searchForNews(query: String) {
        viewModelScope.launch {
            _searchNews.value = Result.Loading
            _searchNews.value = repository.searchForNews(query)
        }
    }
}