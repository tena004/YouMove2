package com.example.youmove2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.youmove2.databinding.ActivityUserprofileBinding

class UserProfileActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityUserprofileBinding
    var prefs: UserPrefs?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityUserprofileBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }
}