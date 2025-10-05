package com.example.clockapp.data.timer

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.clockapp.domain.timer.TimerRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// here i create a datastore instance to store the last total time of the timer called "timer_preferences"
private val Context.dataStore by preferencesDataStore(name = "timer_preferences")

// key to store the last total time in milliseconds i use longPreferencesKey because the total time is in milliseconds and it can be a large number
private val KEY_LAST_TOTAL_TIME = longPreferencesKey("key_last_total_millis")

class TimerRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context
):TimerRepo{



    override suspend fun saveLastTotalTime(totalTime: Long) {
        // edit is a suspend function that allows us to write to the DataStore and i put the total time in Key
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_TOTAL_TIME] = totalTime
        }
    }

    override fun getLastTotalTime(): Flow<Long> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_LAST_TOTAL_TIME] ?: 0L // if there is no value in the DataStore, return 0L as default
        }
    }

}