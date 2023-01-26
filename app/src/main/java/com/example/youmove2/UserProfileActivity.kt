package com.example.youmove2

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivityUserprofileBinding
import java.util.*
import kotlin.collections.ArrayList

class UserProfileActivity: AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var viewBinding: ActivityUserprofileBinding
    var dbase: Baza? = null
    private var tts: TextToSpeech? = null
    private var userData : MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityUserprofileBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        dbase = Baza(this)

        var backgorund = ContextCompat.getDrawable(this, R.drawable.userprofile_bg)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(backgorund)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        tts = TextToSpeech(this,this)
        if(dbase!!.checkTtS()){
            //speakOut(viewBinding.imePrezimeTextView.text.toString() + "Meters walked: " + userData?.get(1).toString() + "Meters run: " + userData?.get(2).toString() + "level:" + userData?.get(3).toString())
        }
        val fontSize= dbase!!.checkFont()
        if(fontSize==1){
            viewBinding.metersTextView.textSize = 16f
            viewBinding.stepsTextView.textSize = 16f
            viewBinding.levelTextView.textSize = 16f
        }else if(fontSize==2){
            viewBinding.metersTextView.textSize = 20f
            viewBinding.stepsTextView.textSize = 20f
            viewBinding.levelTextView.textSize = 20f
        }else{
            viewBinding.metersTextView.textSize = 24f
            viewBinding.stepsTextView.textSize = 24f
            viewBinding.levelTextView.textSize = 24f
        }

        dbase!!.getUserDetails(userData)

        viewBinding.imePrezimeTextView.text = userData?.get(0)
        viewBinding.metersTextView.text = "Prijeđeno metara:  " + userData?.get(1).toString() + "m"
        viewBinding.stepsTextView.text = "Broj koraka:  " + userData?.get(2).toString()
        viewBinding.levelTextView.text = "Razina:  " + userData?.get(3).toString()


        viewBinding.resetBtn.setOnClickListener {
            dbase!!.reset()
            viewBinding.metersTextView.text = "Prijeđeno metara: 0.0 m"
            viewBinding.stepsTextView.text = "Broj koraka: 0"
            viewBinding.levelTextView.text = "Razina: 1"

        }

        viewBinding.logoutBtn.setOnClickListener {
            userData.clear()
            dbase!!.logOut()
            val iHomeScreen = Intent(this@UserProfileActivity, MainActivity::class.java)
            startActivity(iHomeScreen)
        }
    }

    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language specified not supported", Toast.LENGTH_SHORT).show()
            }
        }
    }
}