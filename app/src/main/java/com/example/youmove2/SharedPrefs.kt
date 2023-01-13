package com.example.youmove2

import android.content.Context
import android.content.SharedPreferences


class SharedPrefs(con: Context) {
    var PRIVATE_MODE = 0
    val USER_ID = "id"
    val USERNAME = "username"
    val LOG_IN = "isLoggedIn"
    private val PREF_NAME = "activeUser"
    private val pref: SharedPreferences = con.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    var editor: SharedPreferences.Editor? = pref.edit()

    fun logInSession(name: String?, id: String?) {
        editor!!.putBoolean(LOG_IN, true)
        editor!!.putString(USERNAME, name)
        editor!!.putString(USER_ID, id)
        editor!!.commit()
    }




   /*var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var con: Context? = null

    var PRIVATE_MODE = 0
    private val PREF_NAME = "activeUser"
    val USER_ID = "id"
    val USERNAME = "username"
    val PROJEKT_ID = "projekt_id"
    val LOG_IN = "isLoggedIn"
    val STAVKA = "stavka"

    fun SharedPrefsActive(con : Context) {
        pref = con!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref?.edit()
    }

    fun logInSession(name: String?, id: String?) {
        editor!!.putBoolean(LOG_IN, true)
        editor!!.putString(USERNAME, name)
        editor!!.putString(USER_ID, id)
        editor!!.commit()
    }

    fun projectSession(id: String?) {
        editor!!.putString(PROJEKT_ID, id)
        editor!!.commit()
    }

    fun getUserDetails(context: Context?): HashMap<String, String?>? {
        val user = HashMap<String, String?>()
        user[USERNAME] = pref!!.getString(USERNAME, null)
        user[USER_ID] = pref!!.getString(USER_ID, null)
        return user
    }

    fun getProjectDetails(context: Context?): String? {
        return pref!!.getString(PROJEKT_ID, null)
    }

    fun stavkaSession(stavka: String?) {
        editor!!.putString(STAVKA, stavka)
        editor!!.commit()
    }

    fun getStavka(context: Context?): String? {
        return pref!!.getString(STAVKA, null)
    }

    fun logOutUser() {
        editor!!.clear()
        editor!!.commit()
        val i = Intent(con, MainActivity::class.java)
        con!!.startActivity(i)
    }*/
}

