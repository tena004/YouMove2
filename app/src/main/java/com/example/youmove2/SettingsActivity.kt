package com.example.youmove2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.youmove2.databinding.ActivitySettingsBinding

class SettingsActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingsBinding
    var prefs: UserPrefs?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }
}