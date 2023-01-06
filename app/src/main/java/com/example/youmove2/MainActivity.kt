package com.example.youmove2

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivityMainBinding
import com.google.common.hash.Hashing
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding;
    var session: SharedPrefs? = null
    var con: Context? = null
    var dbase: Baza? = null

    var id: ArrayList<Int>? = null
    var ime: ArrayList<String>? = null
    var korIme: ArrayList<String>? = null
    var lozinka: ArrayList<String>? = null



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
        val iPrijava = Intent(this, MapsActivity::class.java)

        startActivity(iPrijava)
    }

    fun getAllUsers() {
        val cursor: Cursor? = dbase?.readAllUsers()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                id?.add(cursor.getInt(0))
                ime?.add(cursor.getString(1))
                korIme?.add(cursor.getString(2))
                lozinka?.add(cursor.getString(3))
            }
        }
    }

    fun hashPwd(password: String?): String? {
        return Hashing.sha256()
            .hashString(password, StandardCharsets.UTF_8)
            .toString()
    }

}

