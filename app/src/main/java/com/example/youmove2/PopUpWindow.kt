package com.example.youmove2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.graphics.ColorUtils
import com.example.youmove2.databinding.ActivityPopUpWindowBinding
import java.util.*

class PopUpWindow : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var viewBinding : ActivityPopUpWindowBinding
    private var darkStatusBar = false
    //BAZA
    private var dbase : Baza = Baza(this)
    //TTS
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        viewBinding = ActivityPopUpWindowBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(viewBinding.root)

        tts = TextToSpeech(this,this)

        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If you want dark status bar, set darkStatusBar to true
                if (darkStatusBar) {
                    this.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                this.window.statusBarColor = Color.TRANSPARENT
                setWindowFlag(this, false)
            }
        }

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)

        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            viewBinding.popupWindowBackground.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        // Fade animation for the Popup Window
        viewBinding.popupWindowViewWithBorder.alpha = 0f
        viewBinding.popupWindowViewWithBorder.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // Close the Popup Window when you press the button
        viewBinding.popupWindowButtonOk.setOnClickListener {
            onBackPressed()
        }
        viewBinding.popupWindowButtonGeneriraj.setOnClickListener {
            onGeneratePressed()

        }
    }

    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            viewBinding.popupWindowBackground.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        viewBinding.popupWindowViewWithBorder.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if(dbase.checkTtS()) speakOut("Choose your goal. You can start moving.")
                val mapsScreen = Intent(applicationContext, MapsActivity::class.java)
                mapsScreen.putExtra("generate", false)
                startActivity(mapsScreen)
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

    private fun onGeneratePressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            viewBinding.popupWindowBackground.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        viewBinding.popupWindowViewWithBorder.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if(dbase.checkTtS()) speakOut("Your goal is generated. You can start moving.")
                val mapsScreen = Intent(applicationContext, MapsActivity::class.java)
                mapsScreen.putExtra("generate", true)
                startActivity(mapsScreen)
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }

    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
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



