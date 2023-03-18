package com.isfan17.freshnews.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CountryCodePreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val countryCode = stringPreferencesKey("country_code")

    fun getCountryCode(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[countryCode] ?: "us"
        }
    }

    suspend fun saveCountryCode(countryCode: String) {
        dataStore.edit { preferences ->
            preferences[this.countryCode] = countryCode
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CountryCodePreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): CountryCodePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = CountryCodePreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}