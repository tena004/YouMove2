package com.example.youmove2

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

val DATABASE_NAME = "baza.db"
val DATABASE_VERSION = 2

class Baza(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME , null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = "create table korisnik (" + "_id" + " integer primary key autoincrement, " + "ime" + " text not null, " + "kor_ime" + " text not null, " + "lozinka" + " text not null," + "hodanje" + " double default 0," + "trcanje" + " double default 0," + "level" + " integer default 1," + "ulogiran" + " integer default 1);"
        db.execSQL(createUserTable)
        val createUniqueIndex = "CREATE UNIQUE INDEX IF NOT EXISTS korisnik_ui ON korisnik(kor_ime)"
        db.execSQL(createUniqueIndex)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {
        db.execSQL("drop table korisnik")
        Log.i("izmjena_verzije", "dropa tablicu")
        onCreate(db)
    }

    fun registration(username: String, password: String, name: String) : Boolean{
        var db = this.writableDatabase
        val userValues = ContentValues()
        userValues.put("ime", name)
        userValues.put("kor_ime", username)
        userValues.put("lozinka", password)

        var uspjesnaReg = false

        var rowid = db?.insert("korisnik", null, userValues)

        if (rowid != -1L) {
            uspjesnaReg = true
        }

        return uspjesnaReg

    }

    fun checkUser(username: String): Int? {
        val query = "SELECT * FROM korisnik WHERE kor_ime = '$username';"
        var cursor: Cursor? = null
        val db = this.readableDatabase
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        Log.d("VALUE cur = ", cursor?.count.toString());
        return cursor?.count
    }

    fun prijavaProvjera(username: String,  password: String): Boolean {
        val query =
            "SELECT _id FROM korisnik WHERE kor_ime = '$username' AND lozinka = '$password';"
        val db = this.readableDatabase
        var id = -1
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        if (cursor!!.moveToFirst()){
            id = cursor!!.getInt(0)
        }
        if(id == -1){
            return false
        }else{
            val query = "UPDATE korisnik SET ulogiran = 1 WHERE _id = $id;"
            db.rawQuery(query, null)
            return true
        }
    }

    fun getLoggedInUser(): Boolean {
        val query = "SELECT _id FROM korisnik WHERE ulogiran = " + 1 + ";"
        val db = this.readableDatabase
        var id = -1
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        if (cursor!!.moveToFirst()){
            id = cursor!!.getInt(0)
        }
        return id != -1
    }

    fun getUserDetails(array: MutableList<String> = ArrayList()) {
        val query = "SELECT * FROM korisnik WHERE ulogiran = " + 1 + ";"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        var imePrez = ""
        var hodanje = ""
        var trcanje = ""
        var level = ""

        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        if (cursor!!.moveToFirst()){
            imePrez = cursor!!.getString(1)
            hodanje = cursor!!.getFloat(4).toString()
            trcanje = cursor!!.getFloat(5).toString()
            level = cursor!!.getInt(6).toString()
        }

        array.add(imePrez)
        array.add(hodanje)
        array.add(trcanje)
        array.add(level)

        //ova funkcija ne bi trebala vraćati ništa, samo napuniti polje podacima, ovaj string ime je samo za testiranje

    }


    fun logOut(){
        val query = "SELECT _id FROM korisnik WHERE ulogiran = " + 1 + ";"
        val db = this.writableDatabase
        var id = -1
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        if (cursor!!.moveToFirst()){
            id = cursor!!.getInt(0)
        }

        if(id != -1){
            val queryLogOut = "UPDATE korisnik SET ulogiran = 0 WHERE _id = $id;"
            db.rawQuery(queryLogOut, null)

        }
    }

}

