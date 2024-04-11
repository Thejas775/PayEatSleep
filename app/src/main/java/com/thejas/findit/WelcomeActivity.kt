package com.thejas.findit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class WelcomeActivity : AppCompatActivity() {
    private lateinit var signin : Button
    private lateinit var signup : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        signin = findViewById(R.id.signin)
        signup = findViewById(R.id.signup)
        signin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        signup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}