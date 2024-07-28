package com.falcon.otpbymodiji

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class PreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val FIRST_TIME_USER_KEY = booleanPreferencesKey("first_time_user")

    val isFirstTimeUser: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[FIRST_TIME_USER_KEY] ?: true
        }

    suspend fun setFirstTimeUser(isFirstTime: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_TIME_USER_KEY] = isFirstTime
        }
    }
}
