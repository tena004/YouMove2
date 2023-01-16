package com.example.youmove2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.youmove2.databinding.ActivitySettingsBinding
import com.example.youmove2.databinding.ActivitySplashscreenBinding
import kotlinx.coroutines.launch

class SplashScreen: AppCompatActivity() {
    private lateinit var viewBinding: ActivitySplashscreenBinding
    var prefs: UserPrefs?  = null
    var dbase: Baza? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivitySplashscreenBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        dbase = Baza(this)

        var logged = false

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(getDrawable(R.color.black))

        viewBinding.imageView.setImageResource(R.drawable.logo)

        lifecycleScope.launch {
            if(prefs?.getUserId()!=null){ logged = true}
            Toast.makeText(applicationContext, prefs?.getUserId().toString(), Toast.LENGTH_LONG).show()
        }

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