package com.robbyyehezkiel.calisapplication.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.robbyyehezkiel.calisapplication.databinding.ActivityMainBinding
import com.robbyyehezkiel.calisapplication.ui.non_auth.SplashActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        startActivity(Intent(this, SplashActivity::class.java))
    }
}
