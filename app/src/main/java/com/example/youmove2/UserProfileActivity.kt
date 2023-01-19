package com.example.youmove2

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivityUserprofileBinding

class UserProfileActivity: AppCompatActivity() {
    private lateinit var viewBinding: ActivityUserprofileBinding
    var dbase: Baza? = null
    private var userData : MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityUserprofileBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        dbase = Baza(this)

        var backgorund = ContextCompat.getDrawable(this, R.color.white)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(backgorund)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.holo_blue_light)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        dbase!!.getUserDetails(userData)

        viewBinding.imePrezimeTextView.setText(userData?.get(0))

        viewBinding.logoutBtn.setOnClickListener {
            userData.clear()
            dbase!!.logOut()
            val iHomeScreen = Intent(this@UserProfileActivity, MainActivity::class.java)
            startActivity(iHomeScreen)
        }



    }
}