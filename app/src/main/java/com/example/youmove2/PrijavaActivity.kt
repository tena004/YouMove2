package com.example.youmove2

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.youmove2.databinding.ActivityPrijavaBinding
import com.google.common.hash.Hashing
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class PrijavaActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityPrijavaBinding
    var dbase : Baza? = null
    var prefs: UserPrefs?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityPrijavaBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        dbase = Baza(this)

        prefs = UserPrefs(this)

        val window: Window = this.window

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.setBackgroundDrawable(getDrawable(R.color.white))

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        val btnPrijava = viewBinding.buttonPrijava
        btnPrijava.setOnClickListener{
            val korIme = viewBinding.korImePrijava.text.toString()
            val lozinka = viewBinding.lozaPrijava.text.toString()

            if (korIme.isEmpty() ||  lozinka.isEmpty()) {
                Toast.makeText(applicationContext, "Popunite sva polja!", Toast.LENGTH_LONG).show()
            }else{
                if(dbase!!.prijavaProvjera(korIme, hashPwd(lozinka)) == -1) {
                    Toast.makeText(applicationContext,
                        "Pogrešno korisničko ime ili lozinka!",
                        Toast.LENGTH_SHORT).show()
                }else{
                    lifecycleScope.launch{
                        var id = dbase!!.prijavaProvjera(korIme, hashPwd(lozinka))
                        prefs!!.logInSession(id)
                    }
                    val iMapa = Intent(this, MapsActivity::class.java)
                    startActivity(iMapa)
                }
            }
        }
    }

    fun hashPwd(password: String): String {
        return Hashing.sha256()
            .hashString(password, StandardCharsets.UTF_8)
            .toString()
    }
}