package com.example.youmove2

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding;
    var con: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityMainBinding.inflate(layoutInflater)

        val backgorund : Drawable?
        val window: Window = this.window


        backgorund = ContextCompat.getDrawable(this, R.drawable.home_bg)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(backgorund)
        supportActionBar?.hide()

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

    }

    fun prijava(v: View){
        val iPrijava = Intent(this, PrijavaActivity::class.java)
        startActivity(iPrijava)
    }

    fun registracija(v: View){
        val iRegistracija = Intent(this, RegistracijaActivity::class.java)
        startActivity(iRegistracija)
    }
}

