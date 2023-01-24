package com.example.youmove2


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivitySplashscreenBinding

class SplashScreen: AppCompatActivity() {
    private lateinit var viewBinding: ActivitySplashscreenBinding
    var dbase: Baza? = null
    private var logged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivitySplashscreenBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        dbase = Baza(this)

        logged = dbase!!.isLogged()

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(getDrawable(R.color.black))

        viewBinding.imageView.setImageResource(R.drawable.logo)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            if(!logged) {
                val iHomeScreen = Intent(this@SplashScreen, MainActivity::class.java)
                startActivity(iHomeScreen)
                finish()
            }else{
                val iMaps = Intent(this@SplashScreen, MapsActivity::class.java)
                startActivity(iMaps)
                finish()
            }
        }, 3000)
    }
}