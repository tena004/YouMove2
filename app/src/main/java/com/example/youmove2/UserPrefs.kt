package com.example.youmove2

import android.content.Context
import android.content.Intent
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.first

const val PREFERENCE_NAME = "active_user.xml"

class UserPrefs(context: Context){

    val con = context

    private object PreferencesKeys{
        val id = preferencesKey<Int>("id")
        val logged = preferencesKey<Boolean>("logged")
        val fontSize = preferencesKey<String>("fontsize")
        val tts = preferencesKey<Boolean>("tts")
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun logInSession(id: Int){
        dataStore.edit { preference ->
            preference[PreferencesKeys.id] = id
            preference[PreferencesKeys.logged] = true
        }
    }

    suspend fun logOutUser() {
        dataStore.edit { preference ->
            preference.clear()
        }
        val i = Intent(con, MainActivity::class.java)
        con!!.startActivity(i)
    }

    suspend fun getUserId(): Int? {
        val preferences = dataStore.data.first()
        return preferences[PreferencesKeys.id]
    }

    suspend fun settingsTts(tts : Boolean){
        dataStore.edit { preference ->
            preference[PreferencesKeys.tts] = tts
        }
    }

    suspend fun settingsFont(font: String){
        dataStore.edit { preference ->
            preference[PreferencesKeys.fontSize] = font
        }
    }
}


