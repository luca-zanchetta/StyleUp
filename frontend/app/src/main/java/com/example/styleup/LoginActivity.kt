package com.example.styleup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity




class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val loginButton: Button = findViewById(R.id.btnConfirmLogin)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)

        }

    }
}

/*
Logout:
// Get SharedPreferences instance
val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Remove the username
val editor = sharedPreferences.edit()
editor.remove("username")
editor.apply()
 */