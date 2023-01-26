package com.example.youmove2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlin.math.floor

val DATABASE_NAME = "baza.db"
val DATABASE_VERSION = 2

class Baza(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME , null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = "create table korisnik (" + "_id" + " integer primary key autoincrement, " + "ime" + " text not null, " + "kor_ime" + " text not null, " + "lozinka" + " text not null," + "prijedjeno" + " double default 0," + "koraci" + " double default 0," + "level" + " integer default 1," + "ulogiran" + " integer default 1," + "tts" + " integer default 0," + "font" + " integer default 0," + "generiraj" + " integer default 0);"
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
            cursor.close()
        }
        var cursorNew: Cursor? = null
        if(id == -1){
            return false
        }else{
            val query = "UPDATE korisnik SET ulogiran = 1 WHERE _id = $id;"
            cursorNew = db.rawQuery(query, null)
            cursorNew.moveToFirst()
            cursorNew.close()
            return true
        }
    }

    fun isLogged(): Boolean {
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

    fun getLoggedInUser(): Int {
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
        return id
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

    }

    fun logOut(){
        var id = this.getLoggedInUser()
        val queryLogOut = "UPDATE korisnik SET ulogiran = 0 WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.writableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            cursor.close()
        }
    }

    fun ttsOnOff(i: Int){
        var id = this.getLoggedInUser()
        val queryLogOut = "UPDATE korisnik SET tts = $i WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.writableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            cursor.close()
        }
    }

    fun checkTtS(): Boolean{
        var id = this.getLoggedInUser()
        val queryLogOut = "SELECT tts FROM korisnik WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.readableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            var tts = cursor!!.getInt(0)
            cursor.close()
            return  tts != 0
        }else{
            return false
        }
    }

    fun fontSize(i: Int){
        var id = this.getLoggedInUser()
        val queryLogOut = "UPDATE korisnik SET font = $i WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.writableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            cursor.close()
        }
    }

    fun checkFont(): Int{
        var id = this.getLoggedInUser()
        val queryLogOut = "SELECT font FROM korisnik WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.readableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            var font = cursor!!.getInt(0)
            cursor.close()
            return  font
        }else{
            return -1
        }
    }

    fun updateKoraciPrijedeno(k: Float, m: Float){
        var id = this.getLoggedInUser()
        val queryLogOut = "UPDATE korisnik SET prijedjeno = prijedjeno + $m, koraci = koraci + $k WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.writableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            cursor.close()
        }
        updateLevel()
    }


    fun updateLevel(){
        var id = this.getLoggedInUser()
        val query = "SELECT prijedjeno, level FROM korisnik WHERE _id = $id;"
        val db = this.writableDatabase
        var metri = 0f
        var level = 0
        var cursorlevel: Cursor? = null
        if (db != null && id != -1) {
            cursorlevel = db.rawQuery(query, null)
        }
        if(cursorlevel!!.moveToFirst()){
            metri = cursorlevel!!.getFloat(0)
            level = cursorlevel!!.getInt(1)
            cursorlevel.close()
        }
        if(floor(metri/level*1000) ==1f){
            val queryLogOut = "UPDATE korisnik SET level = level + 1 WHERE _id = $id;"
            var cursor: Cursor? = null
            if (db != null && id != -1) {
                cursor = db.rawQuery(queryLogOut, null)
                cursor!!.moveToFirst()
                cursor.close()
            }
        }


    }

    fun reset(){
        var id = this.getLoggedInUser()
        val queryLogOut = "UPDATE korisnik SET prijedjeno = 0, koraci = 0, level = 1 WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.writableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            cursor.close()
        }

    }

    fun generate(boolean: Boolean){
        var id = this.getLoggedInUser()
        var queryLogOut = "UPDATE korisnik SET generiraj = 0 WHERE _id = $id;"
        if(boolean) queryLogOut = "UPDATE korisnik SET generiraj = 1 WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.writableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            cursor.close()
        }

    }

    fun getGenerate(): Int{
        var id = this.getLoggedInUser()
        val queryLogOut = "SELECT generiraj FROM korisnik WHERE _id = $id;"
        var cursor: Cursor? = null
        val db = this.readableDatabase
        if (db != null && id != -1) {
            cursor = db.rawQuery(queryLogOut, null)
            cursor!!.moveToFirst()
            var generiraj = cursor!!.getInt(0)
            cursor.close()
            return  generiraj
        }else{
            return -1
        }
    }


}

