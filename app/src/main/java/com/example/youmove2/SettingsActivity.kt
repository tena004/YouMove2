package com.example.youmove2

import android.os.Bundle
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.view.WindowManager
import android.widget.SeekBar
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
        val fontSize= dbase!!.checkFont()
        if(fontSize==1){
            viewBinding.ttsTxt.textSize = 16f
            viewBinding.textView2.textSize = 16f
        }else if(fontSize==2){
            viewBinding.ttsTxt.textSize = 20f
            viewBinding.textView2.textSize = 20f
        }else{
            viewBinding.ttsTxt.textSize = 24f
            viewBinding.textView2.textSize = 24f
        }

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

        viewBinding.ttsOnOff.isChecked = dbase!!.checkTtS()
        viewBinding.seekBar2.setProgress(fontSize-1)
        viewBinding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
                return
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if(p0?.progress==0){
                    if(dbase!!.checkTtS()) speakOut("Font size set to 16")
                    dbase!!.fontSize(1)
                    viewBinding.ttsTxt.textSize = 16f
                    viewBinding.textView2.textSize = 16f
                    overridePendingTransition(0,0)
                    finish()
                    overridePendingTransition(0,0)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                }
                if(p0?.progress==1){
                    if(dbase!!.checkTtS()) speakOut("Font size set to 20")
                    dbase!!.fontSize(2)
                    viewBinding.ttsTxt.textSize = 20f
                    viewBinding.textView2.textSize = 20f
                    overridePendingTransition(0,0)
                    finish()
                    overridePendingTransition(0,0)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                }
                if(p0?.progress==2){
                    if(dbase!!.checkTtS()) speakOut("Font size set to 24")
                    dbase!!.fontSize(3)
                    viewBinding.ttsTxt.textSize = 24f
                    viewBinding.textView2.textSize = 24f
                    overridePendingTransition(0,0)
                    finish()
                    overridePendingTransition(0,0)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                }
            }
        }
        )
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