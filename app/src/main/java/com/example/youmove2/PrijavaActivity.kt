package com.example.youmove2

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.youmove2.databinding.ActivityPrijavaBinding

class PrijavaActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityPrijavaBinding
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        viewBinding = ActivityPrijavaBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState, persistentState)
        setContentView(viewBinding.root)
    }
}