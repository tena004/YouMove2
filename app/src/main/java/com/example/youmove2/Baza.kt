package com.example.youmove2

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import com.example.youmove2.Baza
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class Baza(private val context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = ("create table korisnik ("
                + "_id" + " integer primary key autoincrement, "
                + "ime" + " text not null, "
                + "kor_ime" + " text not null, "
                + "lozinka" + " text not null,"
                + "hodanje" + " double default 0,"
                + "trcanje" + " double default 0,"
                + "level" + " integer default 1);")
        db.execSQL(createUserTable)
        val createUniqueIndex = "CREATE UNIQUE INDEX IF NOT EXISTS user_ui ON user(username)"
        db.execSQL(createUniqueIndex)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {
        db.execSQL("drop table USER")
        Log.i("izmjena_verzije", "dropa tablicu")
        onCreate(db)
    }

    fun readAllUsers(): Cursor? {
        val query = "SELECT * FROM user;"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        if (db != null) {
            cursor = db.rawQuery(query, null)
        }
        return cursor
    }

    companion object {
        private const val DATABASE_NAME = "userDatabase.db"
        private const val DATABASE_VERSION = 1
    }
}