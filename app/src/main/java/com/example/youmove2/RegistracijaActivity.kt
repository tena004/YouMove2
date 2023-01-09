package com.example.youmove2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.youmove2.databinding.ActivityRegistracijaBinding

class RegistracijaActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityRegistracijaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityRegistracijaBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }

    fun rege(v: View){
        val korIme = viewBinding.korIme.text.toString()
        val imePrez = viewBinding.imePrezime.text.toString()
        val lozinka = viewBinding.lozinka.text.toString()
        val lozProvjera = viewBinding.lozaPon.text.toString()

        

        val iMaps = Intent(this, MapsActivity::class.java)
        startActivity(iMaps)
    }

}