package com.example.youmove2

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivityUserprofileBinding

class UserProfileActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityUserprofileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityUserprofileBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        var backgorund = ContextCompat.getDrawable(this, R.color.white)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(backgorund)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.holo_blue_light)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }
}