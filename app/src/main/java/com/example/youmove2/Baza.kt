package com.example.youmove2

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

val DATABASE_NAME = "baza.db"
val DATABASE_VERSION = 1

class Baza(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME , null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = "create table korisnik (" + "_id" + " integer primary key autoincrement, " + "ime" + " text not null, " + "kor_ime" + " text not null, " + "lozinka" + " text not null," + "hodanje" + " double default 0," + "trcanje" + " double default 0," + "level" + " integer default 1);"
        db.execSQL(createUserTable)
        val createUniqueIndex = "CREATE UNIQUE INDEX IF NOT EXISTS korisnik_ui ON korisnik(kor_ime)"
        db.execSQL(createUniqueIndex)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {
        db.execSQL("drop table korisnik")
        Log.i("izmjena_verzije", "dropa tablicu")
        onCreate(db)
    }

    fun checkUser(username: String): Int? {
        val query = "SELECT * FROM korisnik WHERE kor_ime = '" + username + "';"
        var cursor: Cursor? = null
        val db = this.readableDatabase
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        Log.d("VALUE cur = ", cursor?.count.toString());
        return cursor?.count
    }

    fun readAllUsers(): Cursor? {
        val query = "SELECT * FROM korisnik;"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        return cursor
    }

}

