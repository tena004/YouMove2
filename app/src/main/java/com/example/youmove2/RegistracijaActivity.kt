package com.example.youmove2

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.youmove2.databinding.ActivityRegistracijaBinding
import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets

class RegistracijaActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegistracijaBinding
    var dbase: Baza? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityRegistracijaBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        val btnReg = viewBinding.button
        btnReg.setOnClickListener{
            val korIme = viewBinding.korIme.text.toString()
            val imePrez = viewBinding.imePrezime.text.toString()
            val lozinka = viewBinding.lozinka.text.toString()
            val lozProvjera = viewBinding.lozaPon.text.toString()

            dbase = Baza(this)
            var db = dbase?.writableDatabase

            if (imePrez.length <= 0 || korIme.length <= 0 || lozinka.length <= 0 || lozProvjera.length <= 0) {
                Toast.makeText(applicationContext, "Popunite sva polja!", Toast.LENGTH_LONG).show()
            }
            if (lozinka != lozProvjera) {
                Toast.makeText(applicationContext, "Lozinke se ne podudaraju!", Toast.LENGTH_LONG)
                    .show()
            }
            if((dbase?.checkUser(korIme))!=null){
                Toast.makeText(applicationContext, "Korisničko ime već postoji!", Toast.LENGTH_LONG)
                    .show()
            }else{
                val userValues = ContentValues()
                userValues.put("ime", imePrez)
                userValues.put("kor_ime", korIme)
                userValues.put("lozinka", hashPwd(lozinka))
                var rowid = db?.insert("korisnik", null, userValues)
                if (rowid == -1L) {
                    Toast.makeText(
                        this@RegistracijaActivity,
                        "Neupsješna registracija!",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Uspješno ste registrirani!", Toast.LENGTH_LONG).show()
                    val iMaps = Intent(this, MapsActivity::class.java)
                    startActivity(iMaps)
                }
            }
        }
    }

    fun hashPwd(password: String?): String? {
        return Hashing.sha256()
            .hashString(password, StandardCharsets.UTF_8)
            .toString()
    }

}