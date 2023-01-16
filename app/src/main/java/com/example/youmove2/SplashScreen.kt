package com.example.youmove2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivitySettingsBinding
import com.example.youmove2.databinding.ActivitySplashscreenBinding

class SplashScreen: AppCompatActivity() {
    private lateinit var viewBinding: ActivitySplashscreenBinding
    var prefs: UserPrefs?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivitySplashscreenBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.statusBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this,android.R.color.transparent)
        window.setBackgroundDrawable(getDrawable(R.color.black))

        viewBinding.imageView.setImageResource(R.drawable.logo)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val iHomeScreen = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(iHomeScreen)
            finish()
        }, 3000)
    }
}