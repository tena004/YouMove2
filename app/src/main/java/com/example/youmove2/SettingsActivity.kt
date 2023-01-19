package com.example.youmove2

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.youmove2.databinding.ActivitySettingsBinding
import java.util.*

class SettingsActivity: AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var viewBinding: ActivitySettingsBinding
    private var tts: TextToSpeech? = null
    var dbase: Baza? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        var backgorund = ContextCompat.getDrawable(this, R.drawable.settings_bg)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setBackgroundDrawable(backgorund)

        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)

        var iv = viewBinding.imageView2
        iv.setImageResource(R.drawable.setting2)
        iv.setColorFilter(resources.getColor(R.color.dark_gray))

        dbase = Baza(this)
        tts = TextToSpeech(this,this)

        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.ttsOnOff.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                dbase!!.ttsOnOff(1)
                speakOut("Text to speech on")
            } else {
                dbase!!.ttsOnOff(0)
                speakOut("Text to speech off")
            }
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