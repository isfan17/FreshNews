package com.isfan17.freshnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.isfan17.freshnews.R
import com.isfan17.freshnews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
    }
}