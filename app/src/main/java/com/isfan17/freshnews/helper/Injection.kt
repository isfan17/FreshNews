package com.isfan17.freshnews.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.isfan17.freshnews.data.local.NewsDatabase
import com.isfan17.freshnews.data.network.NewsNetwork
import com.isfan17.freshnews.data.repository.ArticleRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("country_code")

object Injection {
    fun provideRepository(context: Context): ArticleRepository {
        val preferences = CountryCodePreferences.getInstance(context.dataStore)
        val apiService = NewsNetwork.news
        val dao = NewsDatabase.getDatabase(context).articleDao()
        return ArticleRepository.getInstance(preferences, apiService, dao)
    }
}